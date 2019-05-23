FROM redis

COPY ./redis.conf /usr/local/bin/
ENTRYPOINT ["docker-entrypoint.sh"]

EXPOSE 6379
CMD [ "redis-server","/usr/local/bin/redis.conf" ]
