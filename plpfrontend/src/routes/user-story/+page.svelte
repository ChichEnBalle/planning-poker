<script lang="ts">
    import { onMount } from 'svelte';

    let userStories = [];
    let newTitle = '';
    let newDescription = '';

    async function fetchUserStories() {
        const response = await fetch('http://localhost:8080/api/user-stories');
        if (response.ok) {
            userStories = await response.json();
        } else {
            console.error('Failed to fetch user stories');
        }
    }

    async function createUserStory() {
        const response = await fetch('http://localhost:8080/api/user-stories', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ title: newTitle, description: newDescription })
        });

        if (response.ok) {
            const createdStory = await response.json();
            userStories = [...userStories, createdStory];
            newTitle = '';
            newDescription = '';
        } else {
            console.error('Failed to create user story');
        }
    }

    async function addTaskToUserStory(id: number) {
        const story = userStories.find(story => story.id === id);
        if (story) {
            console.log(`Adding task "${story.newTask}" to user story with ID ${id}`);
            const response = await fetch(`http://localhost:8080/api/user-stories/${id}/tasks`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(story.newTask) // Envoyer la nouvelle tâche
            });

            if (response.ok) {
                const updatedStory = await response.json();
                story.tasks = updatedStory.tasks; // Mettre à jour les tâches localement
                story.newTask = ''; // Réinitialiser l'input
            } else {
                console.error('Failed to add task');
            }
        }
    }

    async function deleteUserStory(id) {
        const response = await fetch(`http://localhost:8080/api/user-stories/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            userStories = userStories.filter(story => story.id !== id);
        } else {
            console.error('Failed to delete user story');
        }
    }

    onMount(() => {
        fetchUserStories();
    });
</script>

<div class="flex flex-col bg-white p-7 rounded">
    <h1 class="text-[rgb(51,51,51)]">
        User Stories
    </h1>
    
    <div class="flex">

        <form on:submit|preventDefault={createUserStory} class="w-1/2 mr-3">
            <input type="text" bind:value={newTitle} placeholder="Title" required class="w-full bg-white rounded border border-gray-300 focus:ring-2 focus:ring-[rgb(230,202,147)] text-base outline-none text-gray-700 py-1 px-3 mb-4" />
            <textarea bind:value={newDescription} placeholder="Description" required class="w-full bg-white rounded border border-gray-300 focus:ring-2 focus:ring-[rgb(230,202,147)] text-base outline-none text-gray-700 py-1 px-3 mb-4"></textarea>
            <button type="submit">Create</button>
        </form>
        
        <ul class="w-1/2 ml-3">
            {#each userStories as story}
            <li>
                <strong>{story.title}</strong>: {story.description}
                <ul>
                    {#each story.tasks as task}
                    <li>{task}</li>
                    {/each}
                </ul>
                <input type="text" bind:value={story.newTask} placeholder="New Task"/>
                <button on:click={() => addTaskToUserStory(story.id)}>Add</button>
                <button on:click={() => deleteUserStory(story.id)}>Delete</button>
            </li>
            {/each}
        </ul>
    </div>
        
</div>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Cal+Sans&display=swap');

    h1{
        font-family: 'Cal Sans';
    }

    
</style>