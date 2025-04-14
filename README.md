# Urban Cars

Urban Cars is your gateway to a smarter car shopping experience! Crafted with Kotlin and a robust MVVM architecture,
this sleek Android app lets users effortlessly explore, search, and purchase vehicles — all in a beautifully reactive 
and seamless interface. Whether you're window-shopping or ready to drive away, Urban Cars makes it all just a tap away.

![Screenshot 2025-04-14 124310](https://github.com/user-attachments/assets/f86dfbb6-6a34-44e1-b77a-2e43a64ef3df)![Screenshot 2025-04-14 124835](https://github.com/user-attachments/assets/74b9a56b-5ca7-4169-bd4e-b2bd54aa2486) ![Screenshot 2025-04-14 125321](https://github.com/user-attachments/assets/5a434f57-63bb-430b-b1a0-38e1c7aefb6a)

📦 Project Modules
- User App (UrbanCarsUser)  
     Handles customer-side features like viewing cars, searching, cart, and order tracking.
- Admin App (UrbanCarsAdmin)  
     Interface for dealers/admins to manage car listings.

 ## User Features
- 🔍 Browse & Search Cars: View a wide selection of vehicles with detailed information.
- 🛒 Add to Cart: Users can add multiple vehicles to their cart for easy checkout.
- ✅ Secure Checkout: Complete transactions securely.
- 🔐 Authentication: Firebase Authentication ensures secure login/signup.
- 🔄 Order Tracking: Track your purchases within the app.
- 🔎 Search Functionality: Easily find cars using the search feature.
- 📥 Offline Access: Room Database is used for offline caching of car listings.

![Screenshot 2025-04-14 161849](https://github.com/user-attachments/assets/9be10691-576e-4e68-9e6c-2bfc8d5bd1b3)  ![Screenshot 2025-04-14 161753](https://github.com/user-attachments/assets/38838658-55e0-4894-8ee6-efdbb0a74025)   ![Screenshot 2025-04-14 161708](https://github.com/user-attachments/assets/dca37752-64c5-4e01-8993-404565707042)


## Admin App Features
This project also includes a separate Admin/Dealer Panel App that empowers admins to manage car listings and control inventory in real-time.
- ✅ Authentication – Secure sign-in for admins and dealers.
- ➕ Add New Listings – Easily upload car details with images and pricing.
- ✏️ Edit/Delete Listings – Manage existing cars with full control over inventory.
- 📷 Firebase Storage Integration – Upload and manage car images directly in the cloud.
- 🔄 Real-Time Sync – All changes reflect instantly in the User App using Firebase Realtime Database.
- 📊 Inventory Management – Keep your digital showroom up-to-date from anywhere.

![Screenshot 2025-04-14 125517](https://github.com/user-attachments/assets/c4537829-9f82-4a69-a27d-8ba5f20bbc56)  ![Screenshot 2025-04-14 162011](https://github.com/user-attachments/assets/8f07c6a4-98a3-4d71-b718-602e7f58010c)  ![Screenshot 2025-04-14 162116](https://github.com/user-attachments/assets/b296ed16-36e2-41f0-be47-184574041f6e)


## Tech Stack
- Language: Kotlin
- Architecture: MVVM (Model-View-ViewModel)
- Database: Firebase Realtime Database, Room (for offline support)
- Cloud Storage: Firebase Storage
- Authentication: Firebase Auth
- LiveData + ViewModel: For reactive and lifecycle-aware UI updates

 ## Clone the Repository
### Prerequisites
- Android Studio (latest recommended)  
- Kotlin SDK  
- Firebase project with Realtime Database, Authentication & Storage enabled  

Clone the repository:
## 🚀 Installation
```bash
                                    https://github.com/Piyush-Kumar-Mishra/Urban-Cars.git
```
### 📂 Open in Android Studio
- Open Android Studio.  
- Click on "Open an Existing Project".  
- Navigate to the cloned folder :  
         UrbanCars/
- Let Gradle sync and finish building the project.   

### Set Up Firebase  
Go to Firebase Console  
Create a new project (or use an existing one).  
- Enable:  
  - Authentication (Email/Password)  
  - Realtime Database  
  - Storage  

- Download the google-services.json file.  
Place it inside:  
```bash
UrbanCars/app/google-services.json  
```
### ▶️ Run the App
- Connect your Android device or use an emulator.  
- Click the Run button in Android Studio.
















