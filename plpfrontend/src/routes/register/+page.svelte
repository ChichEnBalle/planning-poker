<script lang="ts">
	let name = '';
	let user: { id: number; name: string } | null = null;
	let errorMessage: string | null = null;

	// Connexion
	async function login() {
		const res = await fetch('http://localhost:8080/api/users/login', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ name })
		});

		if (res.ok) {
			user = await res.json();
			errorMessage = null;
			// Stocker l'utilisateur dans localStorage pour persister la connexion
			localStorage.setItem('user', JSON.stringify(user));
		} else {
			errorMessage = "Utilisateur non trouvé.";
		}
	}

	// Inscription
	async function register() {
		const res = await fetch('http://localhost:8080/api/users/register', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ name })
		});

		if (res.ok) {
			user = await res.json();
			errorMessage = null;
			// Stocker l'utilisateur dans localStorage pour persister la connexion
			localStorage.setItem('user', JSON.stringify(user));
		}
	}
</script>

{#if !user}
	<h2>Login</h2>
	<input type="text" bind:value={name} placeholder="Your name" />
	<button on:click={login}>Login</button>
	<button on:click={register}>Register</button>
	{#if errorMessage}
		<p style="color:red">{errorMessage}</p>
	{/if}
{:else}
	<h2>Welcome, {user.name} (ID: {user.id})</h2>
	<button on:click={() => { localStorage.removeItem('user'); user = null }}>Logout</button>
{/if}


