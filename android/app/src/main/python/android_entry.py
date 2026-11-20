import os
import sys

import mtprotoproxy


def run(secret, on_ready):
    os.environ["PORT"] = "8444"
    os.environ["SECRET"] = secret
    os.environ["LISTEN_ADDR_IPV4"] = "127.0.0.1"
    os.environ["LISTEN_ADDR_IPV6"] = ""
    sys.argv = ["mtprotoproxy"]
    mtprotoproxy.main(on_ready.run, install_signal_handlers=False)


def stop():
    mtprotoproxy.stop_proxy()
