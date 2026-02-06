<!-- README.md -->

<h1 align="center">Crypto Wallet - Android App</h1>
<p align="center"> <strong>Crypto Wallet App</strong> built with Jetpack Compose, Hilt, and Dynamic SDK.</p>

## 🏗️ Project Architecture

The project follows **MVVM (Model-View-ViewModel) architecture** with **Unidirectional Data Flow** and **Repository**:
It was simplified - no **domain** and **data** layers, no **use cases**. But it's still scalable, testable and maintainable for the future growth.
On the next steps it allows to add abstract repositories and use cases, if it's necessary. 
Now it has presentation is separated by screens.


```bash
app/
│── data/
│   ├── di/
│   │   ├── DataModule.kt
│   │
│   ├── AuthWalletRepository.kt
│   ├── AuthRepository.kt
│  
│── presentation/
│   ├── base/
│   │   ├── BaseViewModel.kt
│   │
│   ├── navigation/
│   │   ├── AppNavHost.kt
│   │   ├── AppRoute
│   │
│   ├── screen/
│   │   ├── details/
│   │   ├── login/
│   │   ├── send_transaction/
│   │   ├── splash/
│   │
│   ├── theme/
│
├── CryptoWalletApp.kt
├── MainActivity.kt

```
## ⚙️ Setup & Installation

Clone this repository:
```bash
git clone https://github.com/yourname/crypto_wallet.git
cd CryptoWallet
```

### 🏗 Prerequisites
- **Android Studio Otter 2 or Feature Drop newer**
- **Minimum SDK 33**
- **Gradle version: 9.3.0**

### 🔧 Running the Project

Create and add value into root file **"local.properties"**
```bash
env_id=your_enviroment_id
```
or change into app/build.gradle.kts
```bash
buildConfigField("String", "ENVIRONMENT_ID", "your_enviroment_id")
```

 **Build & Run**
```bash
./gradlew clean build
```
or directly run from **Android Studio**.

---
### Screenshots

<img width="220" height="550" alt="Screenshot_1770364834" src="https://github.com/user-attachments/assets/cfe3ff22-ba8d-4657-b618-21f6952e59c9" />
<img width="220" height="550" alt="Screenshot_1770364604" src="https://github.com/user-attachments/assets/c61c884f-468d-4e99-adf6-72f6fd0ee073" />
<img width="220" height="550" alt="Screenshot_1770379636" src="https://github.com/user-attachments/assets/a8b8da21-3844-47a5-a7aa-ab150291208d" />

### Assumptions 
It shows common way how to organize simple functions with not overcomplicated 
architecture, and provides simple screens with basic crypto functions. But it still requires UX/UI improvements, 
also offline-first strategy, handling requests with low connection and error handling mapping with user-friendly messages. 
And, yes, It still requires test covering. 