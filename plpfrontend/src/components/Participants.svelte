<script lang="ts">
    export let users: { id: number; username: string }[] = [];
</script>

<div class="p-4 bg-gray-100 rounded shadow mb-4 max-w-xs">
    <h3 class="font-bold mb-2">Participants ({users.length})</h3>
    <ul>
        {#each users as user}
            <li class="py-1 px-2 rounded bg-white mb-1 border">{user.username}</li>
        {/each}
    </ul>
</div>