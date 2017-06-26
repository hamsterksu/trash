docker run \
 -it --name=restcomm\
 --net host\
 -e NET_INTERFACE=enp3s0 \
 -e INITIAL_ADMIN_PASSWORD=q1w2e3r4t5\
 -e VOICERSS_KEY=29b2d893df9f454abbfae94df6cff95b\
 -e RESTCOMM_LOGS=/var/log/restcomm\
 -e CORE_LOGS_LOCATION=restcomm_core\
 -e RESTCOMM_TRACE_LOG=restcomm_trace\
 -e MEDIASERVER_LOGS_LOCATION=media_server\
 -e USE_STANDARD_SIP_PORTS=true\
 -e MEDIASERVER_LOWEST_PORT=65000\
 -e MEDIASERVER_HIGHEST_PORT=65535\
 -e LOG_LEVEL=WARN\
 -v /tmp/restcomm/:/var/log/restcomm/\
 -e MS_ADDRESS=192.168.176.58\
 -e MEDIASERVER_EXTERNAL_ADDRESS=192.168.176.58\
 -e MS_EXTERNAL=TRUE\
 restcomm/restcomm:8.0.0 \
 /bin/bash
