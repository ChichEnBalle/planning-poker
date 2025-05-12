<script lang="ts">
    import { onMount } from 'svelte';

    let userStories: any[] = [];
    let newTitle = '';
    let newDescription = '';

    let fileInput: HTMLInputElement;
    let importedTitle = '';
    let importedDesc = '';
    let fileName = '';
    let isFileImported = false;


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

    async function createUserStoryFromImportedData() {
        if (!importedTitle || !importedDesc) {
            console.error('Imported title or description is missing');
            return;
        }

        const response = await fetch('http://localhost:8080/api/user-stories', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ title: importedTitle, description: importedDesc })
        });

        if (response.ok) {
            const createdStory = await response.json();
            userStories = [...userStories, createdStory];
            console.log('User Story created from imported data:', createdStory);
        } else {
            console.error('Failed to create user story from imported data');
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

    async function deleteUserStory(id: any) {
        const response = await fetch(`http://localhost:8080/api/user-stories/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            userStories = userStories.filter(story => story.id !== id);
        } else {
            console.error('Failed to delete user story');
        }
    }

    function handleFileUpload(event) {
        const file = event.target.files[0];
        
        if (!file) return;
        
        if (!file.name.endsWith('.xml')) {
            console.error('Le fichier doit être au format XML');
            return;
        }

        fileName = file.name;
        isFileImported = true;

        const reader = new FileReader();
        
        reader.onload = (e) => {
            const content = e.target.result;
            
            // Parser le XML
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(content, "text/xml");
            
            // Extraire le titre
            const summary = xmlDoc.querySelector('summary');
            if (summary) {
                importedTitle = summary.textContent;
            } else {
                const title = xmlDoc.querySelector('item title');
                if (title) {
                    importedTitle = title.textContent.replace(/^\[[^\]]+\]\s*/, '');
                }
            }
            
            // Extraire la description
            const description = xmlDoc.querySelector('item description');
            if (description) {
                importedDesc = description.textContent.replace(/<\/?p>/g, '');
            }
        };
        
        reader.readAsText(file);
    }

    function resetImport() {
        fileInput.value = '';
        importedTitle = '';
        importedDesc = '';
        fileName = '';
        isFileImported = false;
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

        <!-- Create manually -->
        <form on:submit|preventDefault={createUserStory} class="w-1/2 mr-3 flex flex-col items-center">
            <h2 class="text-[rgb(51,51,51)]">Create </h2>
            <input type="text" bind:value={newTitle} placeholder="Title" required class="w-full bg-white rounded border border-gray-300 focus:ring-2 focus:ring-[rgb(230,202,147)] text-base outline-none text-gray-700 py-1 px-3 mb-4" />
            <textarea bind:value={newDescription} placeholder="Description" required class="w-full bg-white rounded border border-gray-300 focus:ring-2 focus:ring-[rgb(230,202,147)] text-base outline-none text-gray-700 py-1 px-3 mb-4"></textarea>
            <button class="bg-blue-500 text-white py-1 px-3 rounded hover:bg-blue-600 transition-colors " type="submit">Create</button>
        </form>

        <!-- Import XML -->
        <div class="flex flex-col font-sans w-1/2 mr-3">
            <h2 class="text-[rgb(51,51,51)]">Import XML</h2>
            <input 
                type="file" 
                bind:this={fileInput} 
                on:change={handleFileUpload} 
                accept=".xml" 
                class="hidden" 
                id="fileInput"
            />
            
            {#if !isFileImported}
                <button 
                    on:click={() => fileInput.click()} 
                    class="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 transition-colors cursor-pointer"
                >
                    Import XML File
                </button>
            {:else}
                <div class="flex items-center gap-2 p-2 border border-gray-300 rounded bg-gray-50">
                    <span class="flex-1 truncate">
                        {fileName}
                    </span>
                    <button 
                        on:click={resetImport} 
                        class="text-gray-500 hover:text-red-500 text-xl cursor-pointer px-1 leading-none"
                        title="Supprimer le fichier"
                    >
                        &times;
                    </button>
                </div>
            {/if}
            
            
            <div class="mt-4 flex justify-center">
            <button 
                on:click={createUserStoryFromImportedData} 
                class="bg-blue-500 text-white py-1 px-3 rounded hover:bg-blue-600 transition-colors"
            >
                Create User Story from this file
            </button>
            </div>
        </div>
    </div>
    
    <!-- Display User Stories -->
    <div class="mt-8">
        <h2 class="text-[rgb(51,51,51)]">Existing User Stories</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {#each userStories as story}
            <div class="bg-white shadow-md rounded-lg p-4 border border-gray-200">
                <h3 class="text-lg font-bold text-gray-800 mb-2">{story.title}</h3>
                <p class="text-gray-600 mb-4">{story.description}</p>
                <ul class="text-sm text-gray-500 mb-4">
                    {#each story.tasks as task}
                    <li class="list-disc list-inside">{task}</li>
                    {/each}
                </ul>
                <div class="flex gap-2">
                    <input 
                        type="text" 
                        bind:value={story.newTask} 
                        placeholder="New Task" 
                        class="flex-1 bg-gray-50 rounded border border-gray-300 focus:ring-2 focus:ring-blue-500 text-base outline-none text-gray-700 py-1 px-3"
                    />
                    <button 
                        on:click={() => addTaskToUserStory(story.id)} 
                        class="bg-blue-500 text-white py-1 px-3 rounded hover:bg-blue-600 transition-colors"
                    >
                        Add
                    </button>
                </div>
                <button 
                    on:click={() => deleteUserStory(story.id)} 
                    class="mt-4 w-full bg-red-500 text-white py-2 px-4 rounded hover:bg-red-600 transition-colors"
                >
                    Delete
                </button>
            </div>
            {/each}
        </div>
    </div>
</div>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Cal+Sans&display=swap');

    h1{
        font-family: 'Cal Sans';
    }

    button {
        cursor: pointer;
    }

    input[type="file"] {
        margin-bottom: 10px;
    }
</style>