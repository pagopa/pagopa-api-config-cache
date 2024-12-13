from base64 import b64encode
from nacl import encoding, public
import requests, sys


def encrypt(public_key: str, secret_value: str) -> str:
    """Encrypt a Unicode string using the public key."""
    public_key = public.PublicKey(public_key.encode("utf-8"), encoding.Base64Encoder())
    sealed_box = public.SealedBox(public_key)
    encrypted = sealed_box.encrypt(secret_value.encode("utf-8"))
    return b64encode(encrypted).decode("utf-8")


def public_key(token: str, repo: str) -> str:
    """Retrieve public key from token and repository."""
    headers = {
        "Authorization": f"token {token}"
    }
    response = requests.get(f'https://api.github.com/repos/pagopa/{repo}/actions/secrets/public-key', headers=headers)
    return response.json()["key"]


if len(sys.argv) != 3:
    print("how-to: python3 encrypt_github_pat.py <pat> <repo>")

token = sys.argv[1]
repo = sys.argv[2]
public_key = public_key(token, repo)
enc_key = encrypt(public_key, token)

print(enc_key)
