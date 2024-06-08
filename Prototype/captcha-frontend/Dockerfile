# stage1 as builder
FROM node:lts-alpine as build-stage

# Set the log level
ENV LOG_LEVEL=debug

WORKDIR /app

# Copy the package.json and install dependencies
COPY package*.json ./
RUN rm -rf node_modules # package-lock.json
RUN npm cache clean --force
#RUN npm install
RUN npm ci && echo "npm install completed"

# Copy rest of the files
COPY . .

# Build the project
RUN npm run build && echo "npm run build completed"

#stage 2 
FROM nginx:alpine as production-build

COPY ./nginx/default.conf /etc/nginx/conf.d/default.conf

COPY --from=build-stage /app/dist /usr/share/nginx/html

EXPOSE 443
CMD ["nginx", "-g", "daemon off;"]
