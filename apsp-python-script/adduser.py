import firebase_admin
from firebase_admin import credentials, auth

cred = credentials.Certificate('service-key.json')
firebase_admin.initialize_app(cred)

email = input("Enter the user's email: ")
password = input("Enter the user's password: ")

try:
    user = auth.create_user(
        email=email,
        password=password
    )
    print(f"Successfully created user: {user.uid}")

except auth.AuthError as e:
    print(f"Error creating user: {e}")

