<script lang="ts">
	import Card from '../components/Card.svelte';
	import { onMount } from 'svelte';

	let message = "Chargement...";

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

	async function selectCard(card: { id: number; value: string }) {
		selectedCard = card;

		try {
			const res = await fetch('http://localhost:8080/api/votes', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({
					userId: 1,       // à adapter dynamiquement plus tard
					storyId: 42,     // à adapter aussi
					value: card.value
				})
			});
			if (!res.ok) throw new Error("Erreur lors de l'envoi du vote");
			console.log('Vote enregistré !');
		} catch (err) {
			console.error(err);
		}
	}

	async function deselectCard() {
		if (!selectedCard) return;

		try {
			const res = await fetch(`http://localhost:8080/api/votes?userId=1&storyId=42`, {
	method: 'DELETE'
});

			if (!res.ok) throw new Error("Erreur lors de la suppression du vote");
			console.log('Vote supprimé !');
			selectedCard = null;
		} catch (err) {
			console.error(err);
		}
	}
</script>

<h1>{message}</h1>

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
</style>
