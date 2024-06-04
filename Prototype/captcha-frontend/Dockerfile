# stage1 as builder
FROM node:lts-alpine as builder

# Set the log level
ENV LOG_LEVEL=debug

WORKDIR /app

# Copy the package.json and install dependencies
COPY package*.json ./
RUN rm -rf node_modules # package-lock.json
RUN npm cache clean --force
# RUN npm install
RUN npm ci && echo "npm install completed"

# Copy rest of the files
COPY . .

# Build the project
RUN npm run build && echo "npm run build completed"

# List the contents of the dist directory
RUN ls /app/dist

FROM nginx:alpine as production-build

COPY ./nginx/default.conf /etc/nginx/conf.d/default.conf

# Copy from the stage 1
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]