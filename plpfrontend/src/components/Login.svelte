<script lang="ts">
    
    let { handleJoinRoom } = $props();
	let username: string = $state('');
    let room: string = $state('');
    
</script>
			
<style>
	label{
        font-family: 'Cal Sans';
    }
</style> 
<!-- Formulaire de connexion -->
    <div class="mb-4">
        <label for="username" class="block text-sm font-medium text-gray-700">Username</label>
        <input type="text" id="username" bind:value={username} class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
    </div>

    <div class="mb-4">
        <label for="room" class="block text-sm font-medium text-gray-700">Room</label>
        <input type="text" id="room" bind:value={room} class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
    </div>
    <div class="w-full text-center">
        <button 
            onclick={() => handleJoinRoom(username,room)} 
            class="mt-4 bg-[#348449] text-white py-2 px-4 rounded hover:bg-[#1F6838] transform hover:-translate-y-0.5 transition duration-250 cursor-pointer"
            >
            Join Room
        </button>
    </div>