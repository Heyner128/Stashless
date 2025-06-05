FROM node:24 AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build
FROM node:24 AS runner
WORKDIR /app
COPY --from=builder /app/dist ./
EXPOSE 4000
CMD ["node", "stashless/server/server.mjs"]
