ARG NODE_VERSION=22.14.0

FROM node:${NODE_VERSION}-alpine

WORKDIR /usr/src/app

COPY . .

EXPOSE 5173

RUN npm install

CMD npm run dev