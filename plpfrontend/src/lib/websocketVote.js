import { Client } from '@stomp/stompjs';

let client = null;
let voteCallback = null; // Fonction de callback pour l'écoute des messages

export const connectWebSocket = (username, room) => {
    client = new Client({
        brokerURL: 'ws://localhost:8080/play',
        connectHeaders: {
            username: username,
        },
        onConnect: () => {
            console.log('Connected to WebSocket');
            // Abonnement au topic pour recevoir les messages de la room spécifique
            client.subscribe(`/topic/${room}`, (vote) => {
                console.log('Received message:', vote.body);
                if (voteCallback) {
                    // Appeler le callback avec le message reçu
                    voteCallback(JSON.parse(vote.body)); // Assurez-vous que les messages sont parsés en JSON
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
export const sendVote = (vote, room) => {
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

export const sendUnvote = (userId, storyId, room) => {
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
export const addUser = (user, room) => {
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
export const listenForVotes = (callback) => {
    console.log('Setting up message callback');
    voteCallback = callback;
};

