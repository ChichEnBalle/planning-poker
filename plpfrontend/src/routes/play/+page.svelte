<script lang="ts">
    import { onMount } from 'svelte';
    import { connectWebSocket, sendVote, addUser, sendUnvote, listenForVotes } from '$lib/websocketVote.js';
    import UserStories from "../../components/UserStories.svelte"
	import Card from '../../components/Card.svelte';
	import { goto } from '$app/navigation';


    let username = '';
    let room = '';
    let user: { id: number; username: string } | null = null;
    let userId: number | null = null;
    let users: { id: number; username: string }[] = [];
    let storyId = -1; // ID de la story fixe
    let selected = false;
    let votes: { userId: number; storyId: number; value: string }[] = [];
    let hasJoined = false;

    onMount(async () => {
        const storedToken = localStorage.getItem('token');
        console.log("Token envoyé :", storedToken);
        if (!storedToken) {
            goto('/connection');
        }
        else {
            try {
                // Récupérer les informations de l'utilisateur à partir du token
                const res = await fetch('http://localhost:8080/api/users/current', {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${storedToken}`
                    }
                });

                if (res.ok) {
                    user = await res.json();
                    username = user.username;
                    userId = user.id;

                    localStorage.setItem('user', JSON.stringify(user));
                } else {
                    const error = await res.json();
                    console.error("Failed to fetch user info from token :", error.message);
                    goto('/connection');
                }
            } catch (err) {
                console.error("Error fetching user info:", err);
                goto('/connection');
            }
        }

        const storedUsername = localStorage.getItem('username');
        if (storedUsername ) {
            username = storedUsername;;
            hasJoined = true;
            connectWebSocket(username, room);
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
    });

    function getStoryId(cStoryId: number) {
        storyId = cStoryId;
    }

    const handleSendVote = () => {
        if (!selectedCard) {
			warningMessage = "Please select a card before submitting your vote.";
			setTimeout(() => (warningMessage = null), 3000);
			return;
		}

		if (hasVoted) {
			warningMessage = "You already voted.";
			setTimeout(() => (warningMessage = null), 3000);
			return;
		}
        sendVote({ userId,storyId, content: selectedCard.value }, room);
        
        console.log('Vote enregistré !');
        hasVoted = true; // La gestion de hasVoted est à revoir, il faudrait savoir sur quelle userstory un user a voté
    };

    async function handleJoinRoom (cRoom: string){
        room = cRoom;
        if (room.trim() ) {
            console.log(room);
            connectWebSocket(username, room);
            hasJoined = true;
            localStorage.setItem('room', room);
        } else {
            alert("Room name must be filled!");
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
            localStorage.removeItem('token');
            localStorage.removeItem('room');

            goto('/connection');

            // Supprimer l'utilisateur du serveur si nécessaire
            // await fetch(`http://localhost:8080/api/users?userId=${userId}`, {
            //     method: 'DELETE'
            // });
        }
		user = null;
		userId = null;
		hasVoted = false;
		selectedCard = null;
		votes = [];
        username = '';
        room = '';
        hasJoined = false;
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
    {#if !hasJoined}
        <h2 class="text-2xl">Welcome, {username}</h2>
        <div>
            <h3 class="text-xl mb-4">Join a Room</h3>
            <input type="text" bind:value={room} placeholder="Room Name" class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
            <button 
                on:click={() => handleJoinRoom(room)} 
                class="mt-4 bg-[#348449] text-white py-2 px-4 rounded hover:bg-[#1F6838] transform hover:-translate-y-0.5 transition duration-250 cursor-pointer"
            >
                Join Room
            </button>
        </div>
    {:else}
        <div class="flex justify-between items-center">
            <h2 class="text-2xl">Welcome to room {room}, {username}</h2>
            <button 
                on:click={logout}
                class="bg-red-800 text-white py-2 px-4 rounded hover:bg-red-900 transform hover:-translate-y-0.5 transition duration-250 cursor-pointer h-10"
                >
                Logout
            </button>
        </div>

        <UserStories {getStoryId} {room}/>
        {#if storyId != -1}
            <p class="text-center">User story number {storyId} selected.</p>
            <div class="card-deck">
                {#if !hasVoted}
                    <h2>Select a Card</h2>
                    <div class="cards">
                        {#each cards as card (card.id)}
                            <Card value={card.value} selected={selectedCard?.id === card.id} onSelect={() => (selectedCard = card)} />
                        {/each}
                    </div>

                    <!-- If they have already voted, they can see the votes of all users. -->
                    {#if selectedCard}
                        <p>You selected: {selectedCard.value}</p>
                        <button 
                            on:click={handleSendVote}
                            class="mt-4 bg-[#348449] text-white py-2 px-4 rounded hover:bg-[#1F6838] transform hover:-translate-y-0.5 transition duration-250 cursor-pointer"
                        >
                            Submit Vote
                        </button>
                    {/if}

                {:else}
                    <!-- Display the selected card and the votes of all users -->
                    <h2>Votes</h2>
                    {#if votes.length > 0}
                    <ul>
                        {#each votes as vote}
                            <li>
                                {#if users.find(u => u.id === vote.userId)}
                                    {users.find(u => u.id === vote.userId).username}
                                {:else}
                                    User {vote.userId}
                                {/if}
                                voted <div class="border rounded-lg m-[2px] p-[18px] text-center font-bold bg-white inline-block">{vote.value}</div>
                                on {vote.storyId}
                            </li>
                        {/each}
                    </ul>
                    {:else}
                        <p>No votes yet.</p>
                    {/if}

                    <!-- Return to selection of card if you want to change your vote. -->
                    <button 
                        on:click={() => {
                            if (userId && room) {
                                sendUnvote(userId, storyId, room); // 42 est l'id de ta story fixe
                            }
                            hasVoted = false;
                            selectedCard = null;
                        }}
                        class="mt-4 bg-red-800 text-white py-2 px-4 rounded hover:bg-red-900 transform hover:-translate-y-0.5 transition duration-250 cursor-pointer"
                    >
                        Return to selection
                    </button>
                    
                    
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
