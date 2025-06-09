<script lang="ts">
    import { onMount } from 'svelte';
    import {deleteUserStory, addUserStory, listenForUserStories, listenForVotes} from "$lib/websocketVote.js";

    let userStories: any[] = $state([]);
    let newTitle = $state('');
    let newDescription = $state('');

    let fileInput: HTMLInputElement;
    let importedTitle = $state('');
    let importedDesc = $state('');
    let fileName = $state('');
    let isFileImported = $state(false);
    let importedEstimation: number =$state(0);

    const {
        getStoryId,
        room,
        voteHistory = [],
        showHistory = false,
        storyId = -1,
        users = []
    } = $props();


    async function fetchUserStories() {
        if (!room) return;
        const res = await fetch(`http://localhost:8080/api/userstories/${room}`);
        if (res.ok) {
            userStories = await res.json();
        }
    }

    if (room) {
        fetchUserStories();
    }

    function createUserStory() {
        console.log('Creating user story:', newTitle, newDescription);
        addUserStory({title : newTitle, description : newDescription},room);
    }

    async function createUserStoryFromImportedData() {
        if (!importedTitle || !importedDesc) {
            console.error('Imported title or description is missing');
            return;
        }
        addUserStory({title : importedTitle, description : importedDesc, estimation:importedEstimation},room);

    }

    async function addTaskToUserStory(id: number) {
        const story = userStories.find(story => story.id === id);
        if (story) {
            console.log(`Adding task "${story.newTask}" to user story with ID ${id}`);
            const response = await fetch(`http://localhost:8080/api/user-stories/${id}/tasks`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(story.newTask)
            });

            if (response.ok) {
                const updatedStory = await response.json();
                story.tasks = updatedStory.tasks;
                story.newTask = '';
            } else {
                console.error('Failed to add task');
            }
        }
    }


    function deleteUS(ustory) {
        deleteUserStory(ustory, room);
        //userStories = userStories.filter(story => story.id !== ustory.id);
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
            
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(content, "text/xml");
            
            const summary = xmlDoc.querySelector('summary');
            if (summary) {
                importedTitle = summary.textContent;
            } else {
                const title = xmlDoc.querySelector('item title');
                if (title) {
                    importedTitle = title.textContent.replace(/^\[[^\]]+\]\s*/, '');
                }
            }
            
            const description = xmlDoc.querySelector('item description');
            if (description) {
                importedDesc = description.textContent.replace(/<\/?p>/g, '');
            }

            const customfields = xmlDoc.querySelectorAll('customfield');
            customfields.forEach((customfield) => {
                const name = customfield.querySelector('customfieldname');
                if (name && name.textContent === 'Story point estimate') {
                    const value = customfield.querySelector('customfieldvalue');
                    if (value) {
                        importedEstimation = parseInt(value.textContent.toString(), 10);
                        console.log('Estimation :', importedEstimation);
                    }
                }
            });
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

    async function modifyUserStory(id: number, updatedTitle: string, updatedDescription: string) {
        try {
            const response = await fetch(`http://localhost:8080/api/user-stories/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ title: updatedTitle, description: updatedDescription })
            });

            if (response.ok) {
                const updatedStory = await response.json();
                userStories = userStories.map(story =>
                    story.id === id ? updatedStory : story
                );
                console.log('User story updated:', updatedStory);
            } else {
                console.error('Failed to update user story');
            }
        } catch (error) {
            console.error('Error updating user story:', error);
        }
    }

    function exportToCSV(id:any) {
        const csvContent = [
            ['Title', 'Description', 'Estimation', 'Tasks'],
            [
                id.title,
                id.description,
                id.estimation || 'N/A',
                id.tasks.length > 0 ? id.tasks.join('; ') : 'No tasks'
            ]
        ]
            .map(row => row.map(value => `"${value}"`).join(';'))
            .join('\n');

        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const url = URL.createObjectURL(blob);

        const link = document.createElement('a');
        link.href = url;
        link.download = `${id.title.replace(/\s+/g, '_')}_UserStory.csv`;
        link.click();

        URL.revokeObjectURL(url);
    }


    onMount(() => {
        listenForUserStories(async (newUserStory) => {
            console.log('New user story received:', newUserStory);  
            if (newUserStory.title === null) {
                console.log('Deleting la user story:', newUserStory +" id "+ newUserStory.id);
                userStories = userStories.filter(userStory => userStory.id !== newUserStory.id);
            } else {
                userStories = [...userStories, newUserStory];
                console.log('New user story:', newUserStory);
            } 
        });
    });

    export function getTitle(storyId: number): string | undefined {
        return userStories.find(story => story.id === storyId)?.title;
    }
</script>

<div class="flex flex-col p-7 rounded-xl">
    <h1 class="text-[rgb(51,51,51)]">
        User Stories
    </h1>
    
    <div class="flex">

        <!-- Create manually -->
        <form onsubmit={createUserStory} class="w-1/2 mr-3 flex flex-col items-center">
            <h2 class="text-[rgb(51,51,51)] text-[25px]">
                Create
            </h2>
            <input 
                type="text" bind:value={newTitle} 
                placeholder="Title" 
                required 
                class="w-full bg-white rounded border border-gray-300 focus:ring-2 focus:ring-[#8DDDA9] text-base outline-none text-gray-700 py-1 px-3 mb-4"
            />
            <textarea 
                bind:value={newDescription}
                placeholder="Description"
                required
                class="w-full bg-white rounded border border-gray-300 focus:ring-2 focus:ring-[#8DDDA9] text-base outline-none text-gray-700 py-1 px-3 mb-4"
            >
            </textarea>
            <button 
                class="mt-4 bg-[#348449] text-white py-2 px-4 rounded hover:bg-[#1F6838] transform hover:-translate-y-0.5 transition duration-250"
                type="submit"
            >
                Create
            </button>
        </form>

        <!-- Import XML -->
        <div class="flex flex-col font-sans w-1/2 ml-3">
            <h2 class="text-[rgb(51,51,51)] text-[25px]">Import XML</h2>
            <input 
                type="file" 
                bind:this={fileInput} 
                onchange={handleFileUpload} 
                accept=".xml" 
                class="hidden" 
                id="fileInput"
            />
            
            {#if !isFileImported}
                <button 
                    onclick={() => fileInput.click()} 
                    class="px-4 py-2 bg-white text-[rgb(51,51,51)] rounded hover:bg-[#348449] hover:text-white transform hover:-translate-y-0.5 transition duration-250 border border-black"
                >
                    Import XML File
                </button>
            {:else}
                <div class="flex items-center gap-2 p-2 border border-gray-300 rounded bg-gray-50">
                    <span class="flex-1 truncate">
                        {fileName}
                    </span>
                    <button 
                        onclick={resetImport} 
                        class="text-gray-500 hover:text-red-500 text-xl cursor-pointer px-1 leading-none"
                        title="Supprimer le fichier"
                    >
                        &times;
                    </button>
                </div>
            {/if}
            
            
            <div class="mt-4 flex justify-center">
            <button 
                onclick={createUserStoryFromImportedData} 
                class="bg-[#348449] text-white py-2 px-4 rounded hover:bg-[#1F6838] transform hover:-translate-y-0.5 transition duration-250"
            >
                Create User Story from this file
            </button>
            </div>
        </div>
    </div>
    
    <!-- Display User Stories -->
    <div class="mt-6">
        <h2 class="text-[rgb(51,51,51)] text-[25px]">Existing User Stories</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {#each userStories as story}
                <div class="bg-white shadow-md rounded-lg p-4 border border-gray-200">
                    <h3 class="text-lg font-bold text-gray-800 mb-2">{story.title}</h3>
                    <p class="text-gray-600 mb-4">{story.description}</p>
                    {#if story.estimation!=0}
                    <div class="text-gray-700 font-semibold">Points : {story.estimation}</div>
                    {/if}
                    <ul class="text-sm text-gray-500 mb-4">
                        {#each story.tasks as task}
                        <li class="list-disc list-inside">{task}</li>
                        {/each}
                    </ul>
                    <div class="flex justify-around gap-2">
                        <input 
                            type="text" 
                            bind:value={story.newTask} 
                            placeholder="New Task" 
                            class="flex-1 bg-gray-50 w-1/4 rounded border border-gray-300 focus:ring-2 focus:ring-[#8DDDA9] text-base outline-none text-gray-700 py-1 px-3"
                        />
                        <button 
                            onclick={() => addTaskToUserStory(story.id)} 
                            class="bg-[#348449] text-white py-2 px-4 rounded hover:bg-[#1F6838] transform hover:-translate-y-0.5 transition duration-250"
                        >
                            Add
                        </button>
                    </div>
                    <div class="flex">
                        <button 
                        onclick={() => {
                            story.isEditing = !story.isEditing;
                            if (story.isEditing) {
                                story.tempTitle = story.title;
                                story.tempDescription = story.description;
                            }
                        }} 
                            class="mt-4 mr-1 w-1/2 bg-[#348449] text-white py-2 px-4 rounded hover:bg-[#1F6838] transform hover:-translate-y-0.5 transition duration-250"
                            >
                            {story.isEditing ? 'Cancel' : 'Edit'}
                        </button>
                        <button 
                            onclick={() => deleteUS(story)} 
                            class="mt-4 ml-1 w-1/2 bg-red-800 text-white py-2 px-4 rounded hover:bg-red-900 transform hover:-translate-y-0.5 transition duration-250"
                            >
                            Delete
                        </button>
                    </div>

                    <!-- Edit User Story -->
                    {#if story.isEditing}
                    <div class="mt-4 text-center">
                        <input 
                            type="text" 
                            bind:value={story.tempTitle} 
                            placeholder="New Title" 
                            class="w-full bg-gray-50 rounded border border-gray-300 focus:ring-2 focus:ring-[#8DDDA9] text-base outline-none text-gray-700 py-1 px-3 mb-2"
                        />
                        <textarea 
                            bind:value={story.tempDescription} 
                            placeholder="New Description" 
                            class="w-full bg-gray-50 rounded border border-gray-300 focus:ring-2 focus:ring-[#8DDDA9] text-base outline-none text-gray-700 py-1 px-3 mb-2"
                        ></textarea>
                        <button 
                            onclick={() => modifyUserStory(story.id, story.tempTitle, story.tempDescription)} 
                            class="bg-[#348449] text-white py-1 px-3 rounded hover:bg-[#1F6838] transform hover:-translate-y-0.5 transition duration-250"
                        >
                            Save Changes
                        </button>
                    </div>
                    {/if}
                    <button 
                        onclick={() => getStoryId(story.id)}
                        class="mt-3 w-full bg-[#348449] text-white py-2 px-4 rounded hover:bg-[#1F6838] transform hover:-translate-y-0.5 transition duration-250"
                        >
                        Select
                    </button>
                    <button 
                        onclick={() => exportToCSV(story)} 
                        class="px-4 py-2 bg-white text-[rgb(51,51,51)] rounded hover:bg-[#348449] hover:text-white transform hover:-translate-y-0.5 transition duration-250 w-full mt-3 border border-black"
                        >
                            Export to CSV
                    </button>

                    {#if voteHistory && voteHistory.find(h => h.storyId === story.id)}
                        <div class="mt-4 p-4 bg-gray-100 rounded shadow">
                            <h2 class="text-xl font-bold mb-2">Game History</h2>
                            {#each voteHistory.filter(h => h.storyId === story.id) as history}
                                <div class="mb-4">
                                    <ul>
                                        {#each history.votes as v}
                                            <li>
                                                {users.find(u => u.id === v.userId)?.name ?? `User ${v.userId}`} voted <span class="font-bold">{v.value}</span>
                                            </li>
                                        {/each}
                                    </ul>
                                </div>
                            {/each}
                        </div>
                    {/if}
                </div>
            {/each}
        </div>
    </div>
</div>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Cal+Sans&display=swap');

    h1,h2{
        font-family: 'Cal Sans';
    }

    button {
        cursor: pointer;
    }

    input[type="file"] {
        margin-bottom: 10px;
    }
</style>