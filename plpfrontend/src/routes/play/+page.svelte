<script lang="ts">
	let name = '';
	let user: { id: number; name: string } | null = null;
    let userId: number | null = null;
	let errorMessage: string | null = null;

	async function login() {
		const res = await fetch('http://localhost:8080/api/users/login', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ name })
		});

		if (res.ok) {
			user = await res.json();
			userId = user ? user.id : null;
			errorMessage = null;
			localStorage.setItem('user', JSON.stringify(user));
			await fetchVotes();
		} else {
			errorMessage = "Utilisateur non trouvé.";
		}
	}

	async function register() {
		const res = await fetch('http://localhost:8080/api/users/register', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ name })
		});

		if (res.ok) {
			user = await res.json();
			userId = user ? user.id : null;
			errorMessage = null;
			// Stocker l'utilisateur dans localStorage pour persister la connexion
			localStorage.setItem('user', JSON.stringify(user));
		}
	}

	

    // Vote

    import { fade } from 'svelte/transition';
	import Card from '../../components/Card.svelte';

	let hasVoted = false;

    let warningMessage: string | null = null;
	let allVotes: { userId: number; value: string }[] = [];

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


	async function fetchVotes() {
		try {
			const res = await fetch("http://localhost:8080/api/votes?storyId=42");
			if (!res.ok) throw new Error("Erreur lors de la récupération des votes");

			allVotes = await res.json();

			// Vérifier si l'utilisateur connecté a déjà voté
			const userVote = allVotes.find(v => v.userId === userId);
			if (userVote) {
				selectedCard = cards.find(c => c.value === userVote.value) ?? null;
				hasVoted = true; // Marquer l'utilisateur comme ayant voté
			} else {
				selectedCard = null;
				hasVoted = false; // L'utilisateur n'a pas encore voté
			}
		} catch (err) {
			errorMessage = "Erreur lors de la récupération des votes.";
			setTimeout(() => (errorMessage = null), 5000);
			console.error(err);
		}
	}

	async function submitVote() {
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

		try {
			const res = await fetch('http://localhost:8080/api/votes', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({
					userId: userId,
					storyId: 42,
					value: selectedCard.value
				})
			});

			if (!res.ok) throw new Error("Erreur lors de l'envoi du vote");

			console.log('Vote enregistré !');
			hasVoted = true; // Marquer l'utilisateur comme ayant voté
			await fetchVotes(); // Récupérer les votes des autres utilisateurs
		} catch (err) {
			errorMessage = "Erreur lors de l'envoi du vote.";
			setTimeout(() => (errorMessage = null), 5000);
			console.error(err);
		}
	}

	function logout() {
		localStorage.removeItem('user');
		user = null;
		userId = null;
		hasVoted = false;
		selectedCard = null;
		allVotes = [];
	}

	async function resetVote() {
		if (!selectedCard) return;

		try {
			const res = await fetch(`http://localhost:8080/api/votes?userId=${userId}&storyId=42`, {
				method: 'DELETE'
			});

			if (!res.ok) throw new Error("Erreur lors de la suppression du vote");
			console.log('Vote supprimé !');
			selectedCard = null;
			hasVoted = false;
		} catch (err) {
			errorMessage = "Erreur lors de la suppression du vote.";
			setTimeout(() => (errorMessage = null), 5000);
			console.error(err);
		}
	}

</script>


{#if !user}
	<!-- First step, login or register if the user is unknown -->
	<h2>Login</h2>
	<input type="text" bind:value={name} placeholder="Your name" />
	<button on:click={login}>Login</button>
	<button on:click={register}>Register</button>
	{#if errorMessage}
		<p style="color:red">{errorMessage}</p>
	{/if}
{:else}

	<!-- Second step, card selection -->
    <h2>Welcome, {user.name}</h2>
	<button on:click={logout}>Logout</button>
	<div class="card-deck">
		<!-- If the user hasn't voted yet, they can select a card. -->
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
				<button on:click={submitVote}>Submit Vote</button>
			{/if}

		{:else}
			<!-- Display the selected card and the votes of all users -->
			<h2>Votes</h2>
			{#if allVotes.length > 0}
				<ul>
					{#each allVotes as vote (vote.userId)}
						<li>User {vote.userId}: {vote.value}</li>
					{/each}
				</ul>
			{:else}
				<p>No votes yet.</p>
			{/if}

			<!-- Return to selection of card if you want to change your vote. -->
			<button on:click={resetVote}>Return to selection</button>
		{/if}

        {#if warningMessage}
            <p class="warning" transition:fade>{warningMessage}</p>
        {/if}
        {#if errorMessage}
            <p class="error" transition:fade>{errorMessage}</p>
        {/if}
    </div>
{/if}


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
