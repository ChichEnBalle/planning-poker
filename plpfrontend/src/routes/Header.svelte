<script lang="ts">
	export let title: string = "Planning Poker";
</script>

<header>
	<h1>{title}</h1>
	<nav>
		<ul>
			<li><a href="/">Home</a></li>
		</ul>
	</nav>
</header>

<style>
	header {
		background-color: #f8f9fa;
		padding: 1rem;
		text-align: center;
	}

	h1 {
		margin: 0;
	}

	nav ul {
		list-style: none;
		padding: 0;
	}

	nav li {
		display: inline;
		margin: 0 1rem;
	}

	nav a {
		text-decoration: none;
		color: #007bff;
	}

	nav a:hover {
		text-decoration: underline;
	}
</style>