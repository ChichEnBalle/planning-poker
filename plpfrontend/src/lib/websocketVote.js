import { Client } from '@stomp/stompjs';

// @ts-ignore
let client = null;
// @ts-ignore
let voteCallback = null; 
let showVotesCallback = null;
let userStoriesCallback = null; 
let usersCallback = null;
let endVotingCallback = null;

export const connectWebSocket = (username, room) => {
    client = new Client({
        brokerURL: 'ws://localhost:8080/play',
        connectHeaders: {
            username: username,
        },
        onConnect: () => {
            console.log('Connected to WebSocket');

            // S'abonner au topic pour les votes
            client.subscribe(`/topic/${room}`, (message) => {
                const data = JSON.parse(message.body);
                console.log('Received vote message:', data);

                if (voteCallback) {
                    voteCallback(data);
                }
            });

            // S'abonner au topic pour les user stories
            client.subscribe(`/topic/userStory/${room}`, (message) => {
                const data = JSON.parse(message.body);
                console.log('Received user story message:', data);

                if (userStoriesCallback) {
                    userStoriesCallback(data);
                }
            });

            client.subscribe(`/topic/showVotes/${room}`, (message) => {
                const data = JSON.parse(message.body);
                console.log('Received showVotes message:', data);
                if (showVotesCallback) {
                    showVotesCallback(data);
                }
            });

            
            client.subscribe(`/topic/users/${room}`, (message) => {
                const users = JSON.parse(message.body);
                console.log('Received users message:', users);
                if (usersCallback) {
                    usersCallback(users);
                }
            });

            client.subscribe(`/topic/endVoting/${room}`, message => {
                const data = JSON.parse(message.body);
                console.log('Received endVoting message:', data);
                if (endVotingCallback) {
                    endVotingCallback(data);
                }

            });
        },
        onStompError: (error) => {
            console.error('STOMP error', error);
        },
    });

    client.activate();
};

// Fonction pour envoyer un message
// @ts-ignore
export const sendVote = (vote, room) => {
    // @ts-ignore
    if (client && client.connected) {
        console.log('Sending vote:', vote);
        client.publish({
            destination: `/app/play.sendVote/${room}`,
            body: JSON.stringify({
                userId: vote.userId,
                storyId: vote.storyId,
                value: vote.content,
                room: room,
            }),
        });
    }
};

// @ts-ignore
export const sendUnvote = (userId, storyId, room) => {
    // @ts-ignore
    if (client && client.connected) {
        console.log('Sending UNVOTE for user:', userId);
        client.publish({
            destination: `/app/play.unvote/${room}`,
            body: JSON.stringify({
                
                userId: userId,
                storyId: storyId,
                room: room,
            }),
        });
    }
};

// Fonction pour ajouter un utilisateur à une room
// @ts-ignore
export const addUser = (user, room) => {
    // @ts-ignore
    console.log('Adding user:', user, 'to room:', room);
    if (client && client.connected) {
        client.publish({
            destination: `/app/play.addUser/${room}`,
            body: JSON.stringify({
                id: user.id,
                username: user.name,
                roomId: room,
            }),
        });
    }
};

export const showVotesWS = (room, showVotes,userId) => {
    console.log('showVotesWS called with:', room, showVotes, userId);
    if (client && client.connected) {
        client.publish({
            destination: `/app/play.showVotes/${room}`,
            body: JSON.stringify({ room, showVotes, userId }),
        });
    }
};

// Fonction pour écouter les messages entrants
// @ts-ignore
export const listenForVotes = (callback) => {
    console.log('Setting up message callback');
    voteCallback = callback;
};


// @ts-ignore
export const listenForUserStories = (callback) => {
    console.log('Setting up message callback for user stories');
    userStoriesCallback = callback;
};

// @ts-ignore
export const listenForShowVotes = (callback) => {
    console.log('Setting up showVotes callback');
    showVotesCallback = callback;
};

export const listenForUsers = (callback) => {
    console.log('Setting up users callback');
    usersCallback = callback;
};

export const listenForEndVoting = (callback) =>{
    console.log('Setting up endVoting callback');
    endVotingCallback = callback;
};

export const addUserStory = (userStory, room) => {
    // @ts-ignore
    if (client && client.connected) {
        console.log('Adding user story :', userStory);
        client.publish({
            destination: `/app/play.addUserStory/${room}`,
            body: JSON.stringify({
                title: userStory.title,
                description: userStory.description,
                estimation: userStory.estimation,
                id: userStory.id,
                room: room,
            }),
        });
    }
};



export const deleteUserStory = (story, room) => {
    // @ts-ignore
    if (client && client.connected) {
        console.log('Deleting user story :', story);
        client.publish({
            destination: `/app/play.deleteUserStory/${room}`,
            body: JSON.stringify({
                
                id: story.id,
                title : null,
                room: room,
            }),
        });
    }
};

export function sendEndVoting(room, storyId, votes) {
    if (client && client.connected) {
        client.publish({
            destination: `/app/endVoting/${room}`,
            body: JSON.stringify({
                type: "endVoting",
                storyId,
                votes: votes.map(v => ({ userId: v.userId, value: v.value }))
            })
        });
    } else {
        console.error("WebSocket client not connected");
    }
}




export const updateUserStory = (userStory, room) => {
    if (client && client.connected) {
        client.publish({
            destination: `/app/play.updateUserStory/${room}`,
            body: JSON.stringify(userStory),
        });
    }
};

export const addTaskToUserStoryWS = (userStoryId, task, room) => {
    if (client && client.connected) {
        client.publish({
            destination: `/app/play.addTaskToUserStory/${room}`,
            body: JSON.stringify({
                userStoryId,
                task
            }),
        });
    }
};



