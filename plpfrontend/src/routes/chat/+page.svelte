<script>
    import { onMount } from 'svelte';
    import { connectWebSocket, sendMessage, addUser, listenForMessages } from '$lib/websocket.js';

    let username = '';
    let room = '';
    let message = '';
    let messages = [];
    let messageContainer;
    let hasJoined = false;

    // Se connecter au WebSocket quand l'utilisateur soumet ses informations
    onMount(() => {
        
        // Écoute des nouveaux messages
        listenForMessages((newMessage) => {
            messages = [...messages, newMessage];  
            });
        console.log('Component mounted');
    });

    const handleSendMessage = () => {
        if (message.trim() !== '') {
            sendMessage({ sender: username, content: message }, room);
            message = ''; // Effacer le champ de texte après l'envoi
        }
    };

    const handleJoinRoom = () => {
        if (username.trim() && room.trim()) {
            connectWebSocket(username, room);
            addUser(username, room);
            hasJoined = true;
        } else {
            alert("Username and room must be filled!");
        }
    };
</script>

<div class="max-w-2xl mx-auto p-4 bg-white shadow-lg rounded-lg">
    <h2 class="text-2xl font-semibold mb-4 text-center">Join the Chat</h2>
    
    <!-- Formulaire de connexion -->
    <div class="mb-4">
        <label for="username" class="block text-sm font-medium text-gray-700">Username:</label>
        <input type="text" id="username" bind:value={username} class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
    </div>

    <div class="mb-4">
        <label for="room" class="block text-sm font-medium text-gray-700">Room:</label>
        <input type="text" id="room" bind:value={room} class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
    </div>

    <button on:click={handleJoinRoom} class="w-full py-2 bg-blue-600 text-white font-semibold rounded-md hover:bg-blue-700 transition">Join Room</button>

    {#if username && room && hasJoined}
        <!-- Affichage des messages -->
        <div class="mt-6">
            <div bind:this={messageContainer} class="space-y-4 max-h-80 overflow-y-auto">
                {#each messages as { sender, content }}
                    <div class="p-3 border-b border-gray-300 rounded-md">
                        <strong class="font-semibold text-gray-800">{sender}:</strong>
                        <p class="text-gray-600">{content}</p>
                    </div>
                {/each}
            </div>

            <!-- Zone d'envoi de message -->
            <div class="mt-4">
                <input type="text" bind:value={message} placeholder="Type your message..." class="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
                <button on:click={handleSendMessage} class="w-full mt-2 py-2 bg-blue-600 text-white font-semibold rounded-md hover:bg-blue-700 transition">Send</button>
            </div>
        </div>
    {/if}
</div>
