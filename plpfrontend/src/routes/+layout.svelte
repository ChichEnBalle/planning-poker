<script lang="ts">
	import Header from './Header.svelte';
	import '../app.css';
	
	let { children } = $props();
</script>

<div class="app">
	<!-- <Header /> -->

	<div class="background-blur"></div>

	<main>
		{@render children()}
	</main>

	<footer>
	</footer>
</div>

<style>
	.app {
		display: flex;
		flex-direction: column;
		min-height: 100vh;
	}

	:global(body) {
        margin: 0;
        padding: 0;
        background-image: url('.././images/planningPokerBackground2.png');
        background-repeat: repeat;
        background-size: 20%;
		
    }

	.background-blur {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
		min-height: 100vh;
        backdrop-filter: blur(3px);
        background-color: rgba(0, 0, 0, 0.18);
        z-index: -1;
    }

	main {
		flex: 1;
		display: flex;
		flex-direction: column;
		padding: 1rem;
		width: 100%;
		max-width: 64rem;
		margin: 0 auto;
		box-sizing: border-box;
		align-items: center;
	}

	footer {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		/* padding: 12px; */
	}
</style>
