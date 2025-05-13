import { Client } from '@stomp/stompjs';

let client = null;
let messageCallback = null; // Fonction de callback pour l'écoute des messages

export const connectWebSocket = (username, room) => {
    client = new Client({
        brokerURL: 'ws://localhost:8080/chat',
        connectHeaders: {
            username: username,
        },
        onConnect: () => {
            console.log('Connected to WebSocket');
            // Abonnement au topic pour recevoir les messages de la room spécifique
            client.subscribe(`/topic/${room}`, (message) => {
                console.log('Received message:', message.body);
                if (messageCallback) {
                    // Appeler le callback avec le message reçu
                    messageCallback(JSON.parse(message.body)); // Assurez-vous que les messages sont parsés en JSON
                }
            });

            // Ajouter l'utilisateur à la room
            addUser(username, room);
        },
        onStompError: (error) => {
            console.error('STOMP error', error);
        },
    });

    client.activate();
};

// Fonction pour envoyer un message
export const sendMessage = (message, room) => {
    if (client && client.connected) {
        client.publish({
            destination: `/app/chat.sendMessage/${room}`,
            body: JSON.stringify({
                sender: message.sender,
                content: message.content,
                room: room,
            }),
        });
    }
};

// Fonction pour ajouter un utilisateur à une room
export const addUser = (username, room) => {
    if (client && client.connected) {
        client.publish({
            destination: `/app/chat.addUser/${room}`,
            body: JSON.stringify({
                sender: username,
                content: '',
                room: room,
            }),
        });
    }
};

// Fonction pour écouter les messages entrants
export const listenForMessages = (callback) => {
    console.log('Setting up message callback');
    messageCallback = callback;
};
