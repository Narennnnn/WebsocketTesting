import asyncio
import websockets
import time

async def send_ping(websocket):
    try:
        while True:
            client_time = time.time()  # Get the current client time in seconds
            user_message = input("Enter a message to send: ")
            await websocket.send(f'{user_message} {client_time:.12f}')  # Send the user message with client_time
            await asyncio.sleep(5)  # Send Ping every 5 seconds
    except asyncio.CancelledError:
        pass

async def main():
    uri = "ws://localhost:8080/websocket"

    async with websockets.connect(uri) as websocket:
        try:
            print("Connected to the server")

            ping_task = asyncio.create_task(send_ping(websocket))

            while True:
                message = await websocket.recv()
                parts = message.split()
                if len(parts) == 2:
                    server_message = parts[0]
                    server_time = float(parts[1])
                    client_time = time.time()
                    latency_s = abs(client_time - server_time)  # Latency in seconds
                    print(f"Server: {server_message} {server_time:.2f}")
                    latency_s=latency_s*(10**(-12))
                    print(f"Latency: {latency_s:.4f} ms")
                # Handle other messages as needed
        except websockets.exceptions.ConnectionClosed:
            print("Connection closed")

if __name__ == "__main__":
    asyncio.run(main())
