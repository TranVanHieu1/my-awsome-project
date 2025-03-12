# Use the official Node.js image with a specific version
FROM node:16

# Set the working directory inside the container
WORKDIR /usr/src/app

# Copy package.json and package-lock.json to the container
COPY package*.json ./

# Install dependencies
RUN npm install

# Install ts-node and nodemon globally
RUN npm install -g ts-node nodemon

# Copy the rest of the application files
COPY . .

# Expose the port the app runs on
EXPOSE 8000

# Command to run the app with nodemon and ts-node
CMD ["npx", "nodemon", "--exec", "npx ts-node index.ts"]
