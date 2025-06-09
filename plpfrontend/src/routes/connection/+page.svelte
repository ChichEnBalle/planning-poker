<script lang="ts">
	import { goto } from "$app/navigation";

    let username: string = '';
    let password: string = '';
    let isRegistering: boolean = true; 
    let errorMessage: string | null = null;

    async function register(username: string, password: string): Promise<boolean> {
        console.log("Données envoyées :", { username, password });
        const res = await fetch('http://localhost:8080/api/users/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (res.ok) {
            const user = await res.text();
            console.log(user);
            errorMessage = null;
            isRegistering = false;
            return true;
        } else {
            errorMessage = "Failed to create account. Please try again.";
            const error = await res.json();
            console.error("Erreur lors de l'enregistrement :", error.message);
            return false;
        }
    }

    async function login(username: string, password: string): Promise<boolean> {
        const res = await fetch('http://localhost:8080/api/users/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (res.ok) {
            const token = await res.text();
            localStorage.setItem('token', token);
            errorMessage = null;
            goto('/play');
            return true;
        } else {
            errorMessage = "Invalid username or password.";
            return false;
        }
    }

    async function handleSubmit() {
        if (isRegistering) {
            await register(username, password);
        } else {
            await login(username, password);
        }
    }
</script>

<div class="max-w-md mx-auto p-4 bg-white shadow-lg rounded-lg">
    <h2 class="text-2xl font-semibold mb-4 text-center">
        {isRegistering ? 'Create an Account' : 'Login'}
    </h2>

    {#if errorMessage}
        <p class="text-red-500 text-sm mb-4">{errorMessage}</p>
    {/if}

    <div class="mb-4">
        <label for="username" class="block text-sm font-medium text-gray-700">Username</label>
        <input type="text" id="username" bind:value={username} class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
    </div>

    <div class="mb-4">
        <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
        <input type="password" id="password" bind:value={password} class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
    </div>

    <button 
        onclick={async () => {
            await handleSubmit();
            username = '';
            password = '';
        }}
        class="w-full bg-[#348449] text-white py-2 px-4 rounded hover:bg-[#1F6838] transform hover:-translate-y-0.5 transition duration-250 cursor-pointer"
    >
        {isRegistering ? 'Register' : 'Login'}
    </button>

    <p class="mt-4 text-center text-sm">
        {isRegistering ? 'Already have an account?' : "Don't have an account?"}
        <a href="#" onclick={() => {
            isRegistering = !isRegistering;
            username='';
            password='';
        }} class="text-blue-500 hover:underline">
            {isRegistering ? 'Login' : 'Register'}
        </a>
    </p>
</div>

<style>
    h2 {
        font-family: 'Cal Sans';
    }
</style>