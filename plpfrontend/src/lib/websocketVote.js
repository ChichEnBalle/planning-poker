import { Client } from '@stomp/stompjs';

// @ts-ignore
let client = null;
// @ts-ignore
let voteCallback = null; // Fonction de callback pour l'écoute des messages

// @ts-ignore
export const connectWebSocket = (username, room) => {
    client = new Client({
        brokerURL: 'ws://localhost:8080/play',
        connectHeaders: {
            username: username,
        },
        onConnect: () => {
            console.log('Connected to WebSocket');
            // Abonnement au topic pour recevoir les messages de la room spécifique
            // @ts-ignore
            client.subscribe(`/topic/${room}`, (vote) => {
                console.log('Received message:', vote.body);
                // @ts-ignore
                if (voteCallback) {
                    // Appeler le callback avec le message reçu
                    voteCallback(JSON.parse(vote.body)); 
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
                type: 'UNVOTE',
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
    if (client && client.connected) {
        client.publish({
            destination: `/app/play.addUser/${room}`,
            body: JSON.stringify({
                id: user.id,
                name: user.name,
                
            }),
        });
    }
};

// Fonction pour écouter les messages entrants
// @ts-ignore
export const listenForVotes = (callback) => {
    console.log('Setting up message callback');
    voteCallback = callback;
};

