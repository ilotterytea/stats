# stats [![build badge](https://github.com/ilotterytea/stats/actions/workflows/release.yml/badge.svg)](https://github.com/ilotterytea/stats/actions/workflows/release.yml)
Microservice for analyzing Twitch messages for further separation by word usage, third-party emotes.

## Installation guide
1. Configure environment variables in the `.env` file (replace placeholders (*DB_USER, DB_NAME, DB_PASS*) and place the access token from the Twitch Dev app):
```env
DATABASE_URL=postgres://DB_USER:$DB_PASS@db/$DB_NAME
BOT_ACCESS_TOKEN=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
POSTGRES_USER=DB_USER
POSTGRES_DB=DB_NAME
POSTGRES_PASSWORD=DB_PASS
```
2. Start Docker (it must be pre-installed on your host):
```bash
docker-compose up
```
> API will be available at `localhost:8084`. You should do reverse proxy through any web server like Nginx, Apache for better security.

> Also, set password for `/api/v1/join` and `/api/v1/part` via the same web server because these endpoints can add and remove channels from API and Twitch bot.
