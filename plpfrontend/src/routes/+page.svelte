<script lang="ts">
	import { fade } from 'svelte/transition';
	import Card from '../components/Card.svelte';
	import { onMount } from 'svelte';

	let message = "Chargement...";

	let warningMessage: string | null = null;
	let errorMessage: string | null = null; // Message d'erreur global

	let allVotes: { userId: number; value: string }[] = [];
	let userId: number = 1;

	onMount(() => {
		// Récupérer l'userId du localStorage si disponible
		const storedUserId = localStorage.getItem('userId');
		if (storedUserId) {
			userId = parseInt(storedUserId);
		}
		const interval = setInterval(fetchVotes, 2000); // toutes les 2 secondes
		fetchVotes(); // appel initial
		return () => clearInterval(interval);
	});

	// Récupérer le message de bienvenue depuis l'API
	onMount(async () => {
		const res = await fetch("http://localhost:8080/api/hello");
		message = await res.text();
	});

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

	// Fonction pour sélectionner une carte
	async function selectCard(card: { id: number; value: string }) {
		if (selectedCard) {
			warningMessage = "You have already selected a card. Unselect it before choosing another one.";
			setTimeout(() => (warningMessage = null), 3000);
			return;
		}
		selectedCard = card;

		try {
			const res = await fetch('http://localhost:8080/api/votes', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({
					userId: userId,
					storyId: 42,
					value: card.value
				})
			});
			if (!res.ok) throw new Error("Erreur lors de l'envoi du vote");
			console.log('Vote enregistré !');
		} catch (err) {
			errorMessage = "Erreur lors de l'envoi du vote.";
			setTimeout(() => (errorMessage = null), 5000);
			console.error(err);
		}
	}

	// Fonction pour désélectionner la carte
	async function deselectCard() {
		if (!selectedCard) return;

		try {
			const res = await fetch(`http://localhost:8080/api/votes?userId=${userId}&storyId=42`, {
				method: 'DELETE'
			});

			if (!res.ok) throw new Error("Erreur lors de la suppression du vote");
			console.log('Vote supprimé !');
			selectedCard = null;
		} catch (err) {
			errorMessage = "Erreur lors de la suppression du vote.";
			setTimeout(() => (errorMessage = null), 5000);
			console.error(err);
		}
	}

	// Récupérer tous les votes
	async function fetchVotes() {
		try {
			const res = await fetch("http://localhost:8080/api/votes?storyId=42");
			if (!res.ok) throw new Error("Erreur lors de la récupération des votes");

			allVotes = await res.json();

			// Synchroniser selectedCard avec le vote de l'utilisateur actif
			const userVote = allVotes.find(v => v.userId === userId);
			if (userVote) {
				selectedCard = cards.find(c => c.value === userVote.value) ?? null;
			} else {
				selectedCard = null;
			}
		} catch (err) {
			errorMessage = "Erreur lors de la récupération des votes.";
			setTimeout(() => (errorMessage = null), 5000);
			console.error(err);
		}
	}

	// Fonction pour changer d'utilisateur
	function changeUser() {
		userId = userId === 1 ? 2 : 1;
		localStorage.setItem('userId', userId.toString()); // Stocker l'userId dans localStorage
		warningMessage = `You are now User ${userId}`;
		setTimeout(() => (warningMessage = null), 3000);
		selectedCard = null;
		fetchVotes();
	}
</script>

<h1>{message}</h1>

<h2>Active User: {userId}</h2>

<button on:click={() => changeUser()}>Change User</button>

<div class="card-deck">
	<h2>Select a Card</h2>
	<div class="cards">
		{#each cards as card (card.id)}
			<Card value={card.value} selected={selectedCard?.id === card.id} onSelect={() => selectCard(card)} />
		{/each}
	</div>

	{#if selectedCard}
		<p>You selected: {selectedCard.value}</p>
		<button on:click={deselectCard}>Unselect</button>
	{/if}
	{#if warningMessage}
		<p class="warning" transition:fade>{warningMessage}</p>
	{/if}
	{#if errorMessage}
		<p class="error" transition:fade>{errorMessage}</p>
	{/if}
	{#if allVotes.length > 0}
		<h2>Votes</h2>
		<ul>
			{#each allVotes as vote (vote.userId)}
				<li>User {vote.userId}: {vote.value}</li>
			{/each}
		</ul>
	{/if}
	{#if allVotes.length === 0}
		<p>No votes yet.</p>
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

	.cards > * {
		margin: 0.5rem;
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
