<script lang="ts">
	import { goto } from "$app/navigation";

    let username: string = '';
    let password: string = '';
    let isRegistering: boolean = true; 
    let errorMessage: string | null = null;
    let hasAcceptedPolicy: boolean = false;

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
            if (!hasAcceptedPolicy) {
                errorMessage = "You must accept the privacy policy to proceed.";
                return;
            }
            await register(username, password);
        } else {
            await login(username, password);
        }
    }
</script>

<div class="mx-auto p-4 bg-white shadow-lg rounded-lg">
    <h2 class="text-2xl mb-4 text-center">
        {isRegistering ? 'Create an Account' : 'Login'}
    </h2>

    {#if errorMessage}
        <p class="text-red-500 text-sm mb-4">{errorMessage}</p>
    {/if}

    <div class="mb-4">
        <label for="username" class="block text-sm font-medium text-gray-700">Username</label>
        <input type="text" id="username" bind:value={username} class="w-full bg-white rounded-md border border-gray-300 focus:ring-2 focus:ring-[#8DDDA9] text-base outline-none text-gray-700 py-2 px-3 mt-1 shadow-sm sm:text-sm" />
    </div>

    <div class="mb-4">
        <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
        <input type="password" id="password" bind:value={password} class="w-full bg-white rounded-md border border-gray-300 focus:ring-2 focus:ring-[#8DDDA9] text-base outline-none text-gray-700 py-2 px-3 mt-1 mb-4 shadow-sm sm:text-sm" />
    </div>

    {#if isRegistering}
        <div class="mb-4 flex items-start">
            <input type="checkbox" id="accept-policy" bind:checked={hasAcceptedPolicy} class="mt-1 mr-2" />
            <label for="accept-policy" class="text-sm text-gray-700">
                I accept the <a href="/privacy-policy" class="text-blue-500 hover:underline" target="_blank" rel="noopener noreferrer">privacy policy</a>.
            </label>
        </div>
    {/if}

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
        font-weight: lighter;
    }
</style>