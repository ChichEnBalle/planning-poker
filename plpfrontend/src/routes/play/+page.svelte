<script lang="ts">
    import { onMount } from 'svelte';

    import { connectWebSocket, sendVote, addUser, sendUnvote, listenForVotes, listenForUsers, sendEndVoting, listenForEndVoting , listenForShowVotes, showVotesWS} from '$lib/websocketVote.js';

    import Login from "../../components/Login.svelte"
    import UserStories from "../../components/UserStories.svelte"
    import Card from '../../components/Card.svelte';
	import Participants from '../../components/Participants.svelte';



    let username = '';
    let room = '';
    let user: { id: number; name: string } | null = null;
    let userId: number | null = null;
    let users: { id: number; name: string }[] = [];
    let storyId = -1; // ID de la story fixe
    let votes: { userId: number; storyId: number; value: string }[] = [];
    let hasJoined = false;

    let userStoriesRef;
    let allVoted = false;
    let showHistory = false;
    let voteHistory: { storyId: number; votes: { userId: number; value: string }[] }[] = [];

    $: votesForStory = votes.filter(v => v.storyId === storyId);
    $: usersLeftToVote = users.length > 0 && storyId !== -1
        ? users.filter(u => !votesForStory.some(v => v.userId === u.id)).length
        : 0;
    $: allVoted = users.length > 0 && storyId !== -1 && usersLeftToVote === 0;

    let showVotes = false;
    let adminId: number | null = null; 
    



    onMount(async () => {
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
            user = JSON.parse(storedUser);
            username = user.name;
            userId = user.id;
        }

        const storedUsername = localStorage.getItem('username');
        const storedRoom = localStorage.getItem('room');
        if (storedUsername && storedRoom) {
            username = storedUsername;
            room = storedRoom;
            hasJoined = true;
            connectWebSocket(username, room);

            const res = await fetch(`http://localhost:8080/api/rooms/${room}`);
            if (res.ok) {
                const roomData = await res.json();
                adminId = roomData.adminId;
                console.log("Room found:", room + " with adminId: " + adminId);
            }
        }

        if (room) {
            const res = await fetch(`http://localhost:8080/api/users/room/${room}`);
            if (res.ok) {
                users = await res.json();
            }

            listenForUsers( (newUsers) => {
                users = newUsers;
            });
        }

        const res = await fetch(`http://localhost:8080/api/users/room/${room}`);
        if (res.ok) {
            users = await res.json();
        }

        listenForVotes(async (newVote) => {
            if (!users.find(u => u.id === newVote.userId)) {
                try {
                    const res = await fetch(`http://localhost:8080/api/users/${newVote.userId}`);
                    if (res.ok) {
                        const userData = await res.json();
                        users = [...users, userData];
                    }
                } catch (err) {
                    console.error("Erreur lors de la récupération du nom d'utilisateur :", err);
                }
            }

            if (newVote.value === null) {
                votes = votes.filter(vote => vote.userId !== newVote.userId || vote.storyId !== newVote.storyId);
            } else {
                votes = [
                    ...votes.filter(vote => vote.userId !== newVote.userId || vote.storyId !== newVote.storyId),
                    newVote
                ];
            }
            
            
        });
        listenForShowVotes((msg) => {
            showVotes = msg.showVotes;
        });

        listenForEndVoting(room, (data) => {
            voteHistory = [
                ...voteHistory,
                {
                    storyId: data.storyId,
                    votes: data.votes.map(v => ({ userId: v.userId, value: v.value }))
                }
            ];
            showHistory = true;
        });

        
    });

    function getStoryId(cStoryId: number) {
        storyId = cStoryId;
        showHistory = false;
        hasVoted = false;
        selectedCard = null;
        showVotes = false;
    }




    const handleSendVote = () => {
        if (!selectedCard) {
			warningMessage = "Please select a card before submitting your vote.";
			setTimeout(() => (warningMessage = null), 3000);
			return;
		}

		if (hasVoted) {
			warningMessage = "Vous avez déjà voté.";
			setTimeout(() => (warningMessage = null), 3000);
			return;
		}


        if (userId === null) {
            warningMessage = "Erreur : utilisateur non identifié.";
            return;
        }

		votes = [
            ...votes.filter(vote => vote.userId !== userId || vote.storyId !== storyId),
            { userId, storyId, value: selectedCard.value }
        ];
        
        sendVote({ userId,storyId, content: selectedCard.value }, room);
        
        console.log('Vote enregistré !');
        hasVoted = true; // Marquer l'utilisateur comme ayant voté
    };

    async function register(): Promise<boolean> {
        const res = await fetch('http://localhost:8080/api/users/register?roomId=' + room, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name: username })
        });

        if (res.ok) {
            user = await res.json();
            userId = user?.id ?? null;
            localStorage.setItem('user', JSON.stringify(user));
            return true;
        }

        return false;
    }

    async function handleJoinRoom (cUsername: string, cRoom: string){
        username = cUsername;
        room = cRoom;
        if (username.trim() && room.trim()) {
            console.log(username, room);
            if(await register()){
                connectWebSocket(username, room);
                await fetch('http://localhost:8080/api/rooms', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name: room, adminId: userId })
                });
                hasJoined = true;
               
                
                localStorage.setItem('username', username);
                localStorage.setItem('room', room);


                const resUsr = await fetch(`http://localhost:8080/api/users/room/${room}`);
                if (resUsr.ok) {
                    users = await resUsr.json();
                }

                const res = await fetch(`http://localhost:8080/api/rooms/${room}`);
                
                if (res.ok) {
                    const roomData = await res.json();
                    adminId = roomData.adminId;
                    
                    console.log("Room found:", room + " with adminId: " + adminId);

                }
                
            }
            else {
                alert("Username is already taken.");
            }
        } else {
            alert("Username and room must be filled!");
        }
    };


    async function logout() {
        if (userId !== null) {
            votes.forEach(vote => {
                if (vote.userId === userId) {
                    sendUnvote(userId, vote.storyId, room);
                }
            });

            localStorage.removeItem('user');
            localStorage.removeItem('username');
            localStorage.removeItem('room');

            await fetch(`http://localhost:8080/api/users?userId=${userId}&roomId=${room}`, {
                method: 'DELETE'
            });
        }
		localStorage.removeItem('user');
		user = null;
		userId = null;
		hasVoted = false;
		selectedCard = null;
		votes = [];
        username = '';
        room = '';
        hasJoined = false;
        showVotes = false;
	}

    function endVoting() {
        sendEndVoting(room, storyId, votes.filter(v => v.storyId === storyId));
    }
    
    
	

	let hasVoted = false;

    let warningMessage: string | null = null;
	

    // List of the cards to be displayed
	let cards = [
		{ id: 1, value: '1' },
		{ id: 2, value: '2' },
		{ id: 3, value: '3' },
		{ id: 4, value: '5' },
		{ id: 5, value: '8' },
		{ id: 6, value: '10' },
		{ id: 7, value: '15' },
		{ id: 8, value: '20' },
		{ id: 9, value: '30' },
		{ id: 10, value: '?' }
	];

	// Card selected by the user
	let selectedCard: { id: number; value: string } | null = null;


</script>


<div class=" mx-auto p-4 bg-white shadow-lg rounded-lg">
    <h2 class="text-4xl font-semibold mb-4 text-center">Planning Poker</h2>
    {#if !username || !room || !hasJoined}
        <Login {handleJoinRoom}></Login>
    {:else}
        <div class="flex justify-between">
            <h2 class="text-2xl">Welcome, {username}</h2>
            <button 
                on:click={logout}
                class="mt-4 bg-red-800 text-white py-2 px-4 rounded hover:bg-red-900 transform hover:-translate-y-0.5 transition duration-250 cursor-pointer h-10"
                >
                Logout
            </button>
        </div>
        <Participants {users} />

        <UserStories
            bind:this={userStoriesRef}
            {getStoryId}
            {room}
            {voteHistory}
            {showHistory}
            {storyId}
            {users}
        />
        {#if storyId != -1}
            <p class="text-center">User story number {storyId} selected.</p>
            <div class="card-deck">
                {#if !showHistory}
                    {#if !hasVoted}
                        <h2>Select a Card</h2>
                        <div class="cards">
                            {#each cards as card (card.id)}
                                <Card value={card.value} selected={selectedCard?.id === card.id} onSelect={() => (selectedCard = card)} />
                            {/each}
                        </div>
                        {#if selectedCard}
                            <p>You selected: {selectedCard.value}</p>
                            <button 
                                on:click={handleSendVote}
                                class="mt-4 bg-[#348449] text-white py-2 px-4 rounded hover:bg-[#1F6838] transform hover:-translate-y-0.5 transition duration-250 cursor-pointer"
                            >
                                Submit Vote
                            </button>
                        {/if}
                        {#if !allVoted && storyId !== -1}
                            <div class="text-center text-blue-700 font-semibold my-2">
                                {usersLeftToVote} participant(s) still need to vote.
                            </div>
                        {/if}
                        
                    {:else}
                    
                        <!-- Display the selected card and the votes of all users -->
                        <h2>Votes</h2>
                        {#if votes.filter(v => v.storyId === storyId).length > 0}
                            {#if allVoted }
                                <ul>
                                    {#each votes.filter(v => v.storyId === storyId) as vote}
                                        <li>
                                            {#if users.find(u => u.id === vote.userId)}
                                                {users.find(u => u.id === vote.userId).name}
                                            {:else}
                                                User {vote.userId}
                                            {/if}
                                            voted <div class="border rounded-lg m-[2px] p-[18px] text-center font-bold bg-white inline-block">{vote.value}</div>
                                            on {userStoriesRef?.getTitle(vote.storyId) ?? vote.storyId}
                                        </li>
                                    {/each}
                                </ul>
                                {#if userId === adminId}
                                    <button 
                                        on:click={() => showVotesWS(room, false, userId)}
                                        class="mt-4 bg-yellow-600 text-white py-2 px-4 rounded hover:bg-yellow-700 transition duration-250 cursor-pointer">
                                        Hide votes
                                    </button>
                                {/if}
                            {:else}
                                {#if userId === adminId}
                                    <button 
                                        on:click={() => showVotesWS(room, true, userId)}
                                        class="mt-4 bg-[#348449] text-white py-2 px-4 rounded hover:bg-[#1F6838] transform hover:-translate-y-0.5 transition duration-250 cursor-pointer">Show votes</button>
                                {/if}
                            {/if}
                        {:else}
                            <p>No votes yet.</p>
                        {/if}

                        <button 
                            on:click={() => {
                                if (userId && room) {
                                    sendUnvote(userId, storyId, room);
                                }
                                hasVoted = false;
                                selectedCard = null;
                            }}
                            class="mt-4 bg-red-800 text-white py-2 px-4 rounded hover:bg-red-900 transform hover:-translate-y-0.5 transition duration-250 cursor-pointer"
                        >
                            Return to selection
                        </button>
                        {#if !allVoted && storyId !== -1}
                            <div class="text-center text-blue-700 font-semibold my-2">
                                {usersLeftToVote} participant(s) still need to vote.
                            </div>
                        {/if}
                        {#if allVoted && storyId !== -1}
                            <div class="text-center text-green-700 font-bold my-4">
                                All participants have voted! Voting is over.
                            </div>

                            <button
                                on:click={endVoting}
                                class="mt-4 bg-blue-700 text-white py-2 px-4 rounded hover:bg-blue-900 transform hover:-translate-y-0.5 transition duration-250 cursor-pointer"
                                type="button"
                            >
                                End Voting & Show History
                            </button>
                        {/if}
                    {/if}
                        
                
                    
                {/if}
            </div>
        {/if}
    {/if}
</div>

<style>
	.card-deck {
		text-align: center;
	}

	.cards {
		display: flex;
		justify-content: center;
		flex-wrap: wrap;
	}

    h2{
        font-family: 'Cal Sans';
    }

	/* button {
		margin-top: 1rem;
		padding: 0.5rem 1rem;
		background-color: #cc0000;
		color: white;
		border: none;
		border-radius: 8px;
		cursor: pointer;
	}

	button:hover {
		background-color: #a60000;
	} */
	.warning {
		color: #d9534f;
		font-weight: bold;
		margin-bottom: 1rem;
	}

	.error {
		color: #ff0000;
		font-weight: bold;
		margin-bottom: 1rem;
	}
</style>
