<script lang="ts">
    
    let { handleJoinRoom } = $props();
	let username: string = $state('');
    let room: string = $state('');
    
  </script>
			
<style>
			
</style> 
<!-- Formulaire de connexion -->
        <div class="mb-4">
            <label for="username" class="block text-sm font-medium text-gray-700">Username:</label>
            <input type="text" id="username" bind:value={username} class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
        </div>

        <div class="mb-4">
            <label for="room" class="block text-sm font-medium text-gray-700">Room:</label>
            <input type="text" id="room" bind:value={room} class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
        </div>

        <button onclick={() => handleJoinRoom(username,room)} class="w-full py-2 bg-blue-600 text-white font-semibold rounded-md hover:bg-blue-700 transition">Join Room</button>