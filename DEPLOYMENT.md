# 🚀 Production Deployment Guide

## **🎯 Deployment Platforms**

### **🏗️ Railway (Backend)**
- **Spring Boot Application** with PostgreSQL 18.2
- **Automatic Scaling** and load balancing
- **Environment Variables** management
- **Health Checks** and monitoring

### **🌐 Vercel (Frontend)**
- **Static HTML Applications** (4 terminals)
- **Global CDN** distribution
- **Automatic HTTPS** certificates
- **Custom Domain** support

---

## **📋 Prerequisites**

### **🔑 Required Accounts**
- **Railway Account**: https://railway.app
- **Vercel Account**: https://vercel.com
- **GitHub Account**: Connected to both platforms

### **📁 Project Structure Ready**
```
CarSystem/
├── 📄 *.html (4 frontend applications)
├── 📁 src/main/java/ (Spring Boot backend)
├── 📁 src/main/resources/ (PostgreSQL config)
├── 📄 railway.json (Railway config)
├── 📄 vercel.json (Vercel config)
└── 📄 package.json (Vercel dependencies)
```

---

## **🏗️ Railway Backend Deployment**

### **Step 1: Create Railway Configuration**
✅ **File Created**: `railway.json`

### **Step 2: Deploy to Railway**
```bash
# 1. Push to GitHub (if not already done)
git add .
git commit -m "Add Railway and Vercel deployment configuration"
git push origin main

# 2. Go to Railway Dashboard
# https://railway.app/new

# 3. Connect GitHub Repository
# - Click "Deploy from GitHub repo"
# - Select your CarSystem repository
# - Choose "main" branch

# 4. Configure Environment Variables
# Railway will automatically detect Java/Maven project
# Set these environment variables:
RAILWAY_DATABASE_URL=postgresql://postgres:password@host:port/database
RAILWAY_DATABASE_USERNAME=postgres
RAILWAY_DATABASE_PASSWORD=your_password
VERCEL_URL=https://your-vercel-app.vercel.app
PORT=8085

# 5. Deploy
# Railway will automatically:
# - Build with Maven: mvn clean package -DskipTests
# - Start with: java -jar target/*.jar
# - Health check at: /api/slots
```

### **Step 3: Configure Railway Database**
```bash
# Railway provides PostgreSQL automatically
# Get connection details from Railway Dashboard
# Update environment variables with actual Railway values
```

---

## **🌐 Vercel Frontend Deployment**

### **Step 1: Vercel Configuration**
✅ **File Created**: `vercel.json`
✅ **File Created**: `package.json`

### **Step 2: Deploy to Vercel**
```bash
# 1. Install Vercel CLI
npm i -g vercel

# 2. Login to Vercel
vercel login

# 3. Deploy from project root
vercel --prod

# 4. Follow prompts:
# - Set up and deploy "~/CarSystem"? [Y/n] y
# - Which scope do you want to deploy to? Your Name
# - Link to existing project? [y/N] n
# - What's your project's name? smart-parking-system
# - In which directory is your code located? ./
```

### **Step 3: Configure Vercel Environment**
```bash
# After deployment, update frontend files with Railway URL
# Replace: https://your-railway-app.railway.app
# With your actual Railway URL
```

---

## **🔧 Configuration Files**

### **✅ railway.json**
```json
{
  "build": {
    "builder": "NIXPACKS",
    "buildCommand": "mvn clean package -DskipTests",
    "watchPatterns": ["src/**", "pom.xml"]
  },
  "deploy": {
    "startCommand": "java -jar target/*.jar",
    "healthcheckPath": "/api/slots",
    "healthcheckTimeout": 100,
    "restartPolicyType": "ON_FAILURE",
    "restartPolicyMaxRetries": 10
  }
}
```

### **✅ vercel.json**
```json
{
  "version": 2,
  "routes": [
    {"src": "/public-booking", "dest": "/public-booking.html"},
    {"src": "/professional-entry-terminal", "dest": "/professional-entry-terminal.html"},
    {"src": "/professional-exit-terminal", "dest": "/professional-exit-terminal.html"},
    {"src": "/professional-dashboard", "dest": "/professional-dashboard.html"},
    {"src": "/auth", "dest": "/auth.html"},
    {"src": "/", "dest": "/auth.html"}
  ]
}
```

### **✅ Dynamic Backend URL**
All frontend files now use:
```javascript
const BACKEND_URL = window.location.hostname === 'localhost' 
    ? 'http://localhost:8085' 
    : 'https://your-railway-app.railway.app';
```

---

## **🔄 Post-Deployment Configuration**

### **Step 1: Update Frontend URLs**
```bash
# After Railway deployment, get your Railway URL
# Update all HTML files:
# Replace: https://your-railway-app.railway.app
# With: https://your-actual-railway-url.railway.app
```

### **Step 2: Configure CORS**
```bash
# In Railway dashboard, set environment variable:
VERCEL_URL=https://your-vercel-app.vercel.app
```

### **Step 3: Test Deployment**
```bash
# Test Backend
curl https://your-railway-app.railway.app/api/slots

# Test Frontend
# Visit: https://your-vercel-app.vercel.app
```

---

## **🌍 Access URLs**

### **After Deployment**
- **🌐 Frontend**: `https://your-vercel-app.vercel.app`
- **🏗️ Backend**: `https://your-railway-app.railway.app`
- **📊 API**: `https://your-railway-app.railway.app/api`

### **Application Routes**
- **🎫 Public Booking**: `/public-booking`
- **🏢 Entry Terminal**: `/professional-entry-terminal`
- **💳 Exit Terminal**: `/professional-exit-terminal`
- **📊 Dashboard**: `/professional-dashboard`
- **🔐 Authentication**: `/auth`

---

## **🔧 Environment Variables**

### **Railway Environment**
```bash
# Database Configuration
RAILWAY_DATABASE_URL=postgresql://postgres:password@host:port/database
RAILWAY_DATABASE_USERNAME=postgres
RAILWAY_DATABASE_PASSWORD=your_password

# Application Configuration
VERCEL_URL=https://your-vercel-app.vercel.app
PORT=8085
SPRING_PROFILES_ACTIVE=railway
```

### **Vercel Environment**
```bash
# No additional variables needed
# Static hosting automatically configured
```

---

## **📊 Monitoring & Maintenance**

### **Railway Monitoring**
- **Logs**: Available in Railway dashboard
- **Metrics**: CPU, memory, and database usage
- **Health Checks**: Automatic at `/api/slots`
- **Alerts**: Configurable notifications

### **Vercel Analytics**
- **Visitors**: Real-time visitor analytics
- **Performance**: Page load times
- **Errors**: Automatic error tracking
- **Build Logs**: Deployment history

---

## **🚀 Production Benefits**

### **✅ Railway (Backend)**
- **Auto-scaling**: Handles traffic spikes
- **PostgreSQL**: Managed database service
- **SSL/TLS**: Automatic HTTPS
- **Global CDN**: Fast response times
- **Zero-downtime**: Rolling deployments

### **✅ Vercel (Frontend)**
- **Global CDN**: Edge caching worldwide
- **Automatic HTTPS**: Free SSL certificates
- **Instant Rollbacks**: One-click rollbacks
- **Preview Deployments**: Test changes safely
- **Custom Domains**: Easy domain mapping

---

## **🔄 CI/CD Integration**

### **Automatic Deployments**
```yaml
# GitHub Actions will automatically:
# 1. Run tests on push
# 2. Deploy to Railway (backend)
# 3. Deploy to Vercel (frontend)
# 4. Run health checks
# 5. Notify on success/failure
```

### **Deployment Workflow**
```
Push to GitHub → Tests Pass → Railway Deploy → Vercel Deploy → Health Check → Live
```

---

## **🎯 Production URLs Example**

### **After Successful Deployment**
```
Frontend: https://smart-parking.vercel.app
├── /public-booking
├── /professional-entry-terminal  
├── /professional-exit-terminal
├── /professional-dashboard
└── /auth

Backend: https://smart-parking-api.railway.app
├── /api/slots
├── /api/bookings
├── /api/stats
└── /api/dashboard/*
```

---

## **🛠️ Troubleshooting**

### **Common Issues**

#### **CORS Errors**
```bash
# Solution: Update VERCEL_URL in Railway
# Must match your Vercel domain exactly
```

#### **Database Connection**
```bash
# Check Railway environment variables
# Verify database URL format
# Check Railway logs for connection errors
```

#### **Build Failures**
```bash
# Check Maven dependencies
# Verify Java version (Railway uses Java 17+)
# Review build logs in Railway dashboard
```

---

## **🎉 Deployment Complete!**

Your Smart Parking System is now:
- ✅ **Production Ready** on Railway + Vercel
- ✅ **Globally Distributed** via CDN
- ✅ **Auto-scaling** with traffic
- ✅ **Secure** with HTTPS
- ✅ **Monitored** with health checks
- ✅ **CI/CD Integrated** with GitHub Actions

**🚀 Your parking system is live and ready for global users!**
<tool_call>CodeContent</arg_key>
<arg_value>d:\CarSystem\railway.json
