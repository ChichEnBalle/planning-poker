<script lang="ts">
    import { onMount } from 'svelte';
    import { connectWebSocket, sendVote, addUser, sendUnvote, listenForVotes } from '$lib/websocketVote.js';
    import Login from "../../components/Login.svelte"
    import UserStories from "../../components/UserStories.svelte"



    let username = '';
    let room = '';
    let user: { id: number; name: string } | null = null;
    let userId: number | null = null;
    let users: { id: number; name: string }[] = [];
    let storyId = 42; // ID de la story fixe

    
    let votes: { userId: number; storyId: number; value: string }[] = [];
   
    let hasJoined = false;

    // Se connecter au WebSocket quand l'utilisateur soumet ses informations
    onMount(() => {
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
            user = JSON.parse(storedUser);
            username = user.name;
            userId = user.id;
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
			warningMessage = "Vous avez déjà voté.";
			setTimeout(() => (warningMessage = null), 3000);
			return;
		}

		
            
            sendVote({ userId,storyId, content: selectedCard.value }, room);
            
			console.log('Vote enregistré !');
			hasVoted = true; // Marquer l'utilisateur comme ayant voté
			
    };
    async function register(): Promise<boolean> {
        const res = await fetch('http://localhost:8080/api/users/register', {
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
                // Attendre que l’utilisateur soit bien enregistré
                connectWebSocket(username, room); // Ensuite seulement, se connecter
                hasJoined = true;

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
        // Envoyer un message de "dé-vote" pour chaque vote de l'utilisateur
        votes.forEach(vote => {
            if (vote.userId === userId) {
                sendUnvote(userId, vote.storyId, room); // Ici, 42 est l'ID de la story
            }
        });

        // Supprimer l'utilisateur du serveur si nécessaire
        await fetch(`http://localhost:8080/api/users?userId=${userId}`, {
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
	}

   

    import { fade } from 'svelte/transition';
	import Card from '../../components/Card.svelte';

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


<div class="max-w-2xl mx-auto p-4 bg-white shadow-lg rounded-lg">
    <h2 class="text-2xl font-semibold mb-4 text-center">Join the Chat</h2>
    {#if !username || !room || !hasJoined}
        <Login {handleJoinRoom}></Login>
    {:else}
        <h2>Welcome, {username}</h2>
        <button on:click={logout}>Logout</button>

        <UserStories {getStoryId} {room}/>
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
                    <button on:click={handleSendVote}>Submit Vote</button>
                {/if}

		    {:else}
                <!-- Display the selected card and the votes of all users -->
                <h2>Votes</h2>
                {#if votes.length > 0}
                <ul>
                    {#each votes as vote}
                        <li>
                            {#if users.find(u => u.id === vote.userId)}
                                {users.find(u => u.id === vote.userId).name}
                            {:else}
                                User {vote.userId}
                            {/if}
                            : {vote.value} on {vote.storyId}
                        </li>
                    {/each}
                </ul>
                {:else}
                    <p>No votes yet.</p>
                {/if}

                <!-- Return to selection of card if you want to change your vote. -->
                <button on:click={() => {
                    if (userId && room) {
                        sendUnvote(userId, storyId, room); // 42 est l'id de ta story fixe
                    }
                    hasVoted = false;
                    selectedCard = null;
                }}>
                    Return to selection
                </button>
                
                
		    {/if}
    
        
    
</div>
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


	button {
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
	}
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
