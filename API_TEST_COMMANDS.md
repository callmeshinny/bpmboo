# BPMBoo API Test Commands

## Environment Variables
Replace these values before running:
- `USER_ID` - Your user ID
- `RECORD_ID` - A heart rate record ID
- `TOKEN` - Your JWT token from login/OTP

## Local Testing (http://localhost:5001)
Replace `https://bpmboo-backend.onrender.com` with `http://localhost:5001` if testing locally.

---

## Auth Endpoints

### Register
```bash
curl -X POST http://localhost:5001/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Password123!"
  }'
```

### Login
```bash
curl -X POST http://localhost:5001/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Password123!"
  }'
```

---

## OTP Endpoints

### Request OTP
```bash
curl -X POST http://localhost:5001/api/otp/request \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com"
  }'
```

### Verify OTP
```bash
curl -X POST http://localhost:5001/api/otp/verify \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "code": "123456"
  }'
```

---

## Profile Endpoints

### Get Profile
```bash
curl https://bpmboo-backend.onrender.com/api/profile/USER_ID \
  -H "Authorization: Bearer TOKEN"
```

### Update Full Profile
```bash
curl -X PUT https://bpmboo-backend.onrender.com/api/profile/USER_ID \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "fullName": "John Doe",
    "phone": "+1234567890",
    "dob": "1990-01-01",
    "gender": "male",
    "emergencyName": "Jane Doe",
    "emergencyPhone": "+0987654321",
    "avatarUrl": "https://example.com/avatar.jpg"
  }'
```

### Update Avatar (PATCH)
```bash
curl -X PATCH https://bpmboo-backend.onrender.com/api/profile/USER_ID/avatar \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "avatarUrl": "https://example.com/new-avatar.jpg"
  }'
```

---

## Heart Rate Endpoints

### Create Heart Rate Record
```bash
curl -X POST https://bpmboo-backend.onrender.com/api/heart-rates \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "userId": "USER_ID",
    "bpmValue": 75,
    "feelingTag": "Resting",
    "note": "Measured after waking up"
  }'
```

### Get All Records for User
```bash
curl https://bpmboo-backend.onrender.com/api/heart-rates/USER_ID \
  -H "Authorization: Bearer TOKEN"
```

### Get Latest Heart Rate Record
```bash
curl https://bpmboo-backend.onrender.com/api/heart-rates/USER_ID/latest \
  -H "Authorization: Bearer TOKEN"
```

### Get Last N Records (with limit)
```bash
# Get last 5 records
curl "https://bpmboo-backend.onrender.com/api/heart-rates/USER_ID/take?limit=5" \
  -H "Authorization: Bearer TOKEN"

# Get last 10 records (default)
curl "https://bpmboo-backend.onrender.com/api/heart-rates/USER_ID/take" \
  -H "Authorization: Bearer TOKEN"
```

### Get Heart Rate Statistics
```bash
curl https://bpmboo-backend.onrender.com/api/heart-rates/USER_ID/stats \
  -H "Authorization: Bearer TOKEN"
```

### Update Heart Rate Record
```bash
curl -X PUT https://bpmboo-backend.onrender.com/api/heart-rates/RECORD_ID \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "bpmValue": 88,
    "feelingTag": "After exercise",
    "note": "Updated measurement"
  }'
```

### Delete Heart Rate Record
```bash
curl -X DELETE https://bpmboo-backend.onrender.com/api/heart-rates/RECORD_ID \
  -H "Authorization: Bearer TOKEN"
```

---

## Insight Endpoints

### Get AI Summary
```bash
curl https://bpmboo-backend.onrender.com/api/insight/summary/USER_ID \
  -H "Authorization: Bearer TOKEN"
```

---

## Testing Workflow

1. **Get Token**: Register/Login to get TOKEN
2. **Create Profile**: Update your profile with full information
3. **Create Records**: Create multiple heart rate records with different values
4. **Test CRUD Operations**:
   - GET /latest → Get newest record
   - GET /take?limit=5 → Get 5 newest records
   - PUT /{recordId} → Update a record
   - DELETE /{recordId} → Delete a record
5. **Check Stats**: GET /stats to see aggregated data
6. **Update Avatar**: PATCH /avatar to update only the avatar

---

## Useful curl Flags
- `-v` or `--verbose`: Show full request/response details
- `-s` or `--silent`: Silent mode (no progress bar)
- `-X METHOD`: Specify HTTP method (GET, POST, PUT, DELETE, PATCH)
- `-H "Header: Value"`: Add header
- `-d 'JSON'`: Send JSON body
- `-o filename`: Save response to file
- `| jq .`: Pretty print JSON (requires jq installed)

Example with pretty print:
```bash
curl https://bpmboo-backend.onrender.com/api/heart-rates/USER_ID/latest \
  -H "Authorization: Bearer TOKEN" | jq .
```
