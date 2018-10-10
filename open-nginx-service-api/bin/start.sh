docker run -d --name="openresty" \
        -p 12344:8021 -p 12345:8022 \
        -v /home/openresty/app-test/nginx_conf:/usr/local/openresty/nginx/conf \
        -v /home/openresty/app-test/lualib:/usr/local/openresty/lualib \
        -v /home/openresty/app-test/logs:/data/logs \
        -v /home/openresty/app-test/apps:/data/apps \
        openresty/openresty:1.9.15.1-trusty
