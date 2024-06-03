# stage1 as builder
FROM node:lts-alpine as builder

WORKDIR /app

# Copy the package.json and install dependencies
COPY package*.json ./
RUN npm install && echo "npm install completed"

# Copy rest of the files
COPY . .

# Build the project
RUN npm run build && echo "npm run build completed"


FROM nginx:alpine as production-build

COPY ./nginx/default.conf /etc/nginx/conf.d/default.conf

# Copy from the stage 1
COPY --from=builder /app/dist /usr/share/nginx/html
COPY --from=builder /app/node_modules /usr/share/nginx/html/node_modules

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]