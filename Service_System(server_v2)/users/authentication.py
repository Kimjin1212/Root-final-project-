from rest_framework.authentication import BaseAuthentication
from rest_framework.exceptions import AuthenticationFailed
from .utils import decode_jwt

class JWTAuthentication(BaseAuthentication):
    def authenticate(self, request):
        token = request.headers.get('Authorization')
        if not token:
            return None
        
        token = token.replace('Bearer ', '')
        user = decode_jwt(token)
        if not user:
            raise AuthenticationFailed('Invalid or expired token')
        
        return (user, None)
