# API Enhancements Summary

## Overview
Added complete CRUD operations for Heart Rate and Profile endpoints to provide a comprehensive API for demonstration purposes.

---

## Changes Made

### 1. Heart Rate Controller (`src/controllers/heartRateController.js`)

**New Methods Added:**

#### `getLatestHeartRateRecord`
- Retrieves the most recent heart rate record for a user
- Endpoint: `GET /api/heart-rates/{userId}/latest`
- Returns the single latest record or 404 if none found

#### `getTakeHeartRateRecords`
- Retrieves the last N heart rate records (paginated)
- Endpoint: `GET /api/heart-rates/{userId}/take?limit=5`
- Query parameter `limit` controls number of records (max 100, default 10)
- Useful for showing recent measurements

#### `updateHeartRateRecord`
- Updates an existing heart rate record
- Endpoint: `PUT /api/heart-rates/{recordId}`
- Can update: `bpmValue`, `feelingTag`, `note`
- Returns 404 if record not found
- Uses partial update pattern (only provided fields are updated)

**Existing Methods:**
- `createHeartRateRecord` - POST /api/heart-rates
- `getHeartRateRecordsByUser` - GET /api/heart-rates/{userId}
- `deleteHeartRateRecord` - DELETE /api/heart-rates/{recordId}
- `getHeartRateStats` - GET /api/heart-rates/{userId}/stats

---

### 2. Profile Controller (`src/controllers/profileController.js`)

**New Method Added:**

#### `updateProfileAvatar`
- Updates only the user's avatar URL
- Endpoint: `PATCH /api/profile/{userId}/avatar`
- Validates that `avatarUrl` is provided
- Returns updated user object
- More efficient than PUT for single field updates

**Existing Methods:**
- `getProfile` - GET /api/profile/{userId}
- `updateProfile` - PUT /api/profile/{userId} (full profile update)

---

### 3. Heart Rate Routes (`src/routes/heartRateRoutes.js`)

**Route Order (important for Express matching):**
1. `POST /api/heart-rates` - Create record
2. `GET /api/heart-rates/{userId}` - Get all records
3. `GET /api/heart-rates/{userId}/stats` - Get statistics
4. `GET /api/heart-rates/{userId}/latest` - Get latest record
5. `GET /api/heart-rates/{userId}/take` - Get last N records
6. `PUT /api/heart-rates/{recordId}` - Update record
7. `DELETE /api/heart-rates/{recordId}` - Delete record

**Note:** More specific routes (`/stats`, `/latest`, `/take`) are defined before generic `/{recordId}` routes.

---

### 4. Profile Routes (`src/routes/profileRoutes.js`)

**Updated Routes:**
1. `GET /api/profile/{userId}` - Get profile
2. `PUT /api/profile/{userId}` - Update full profile
3. `PATCH /api/profile/{userId}/avatar` - Update avatar only

**Route Method Distinction:**
- `PUT` - Full resource update (replacement)
- `PATCH` - Partial resource update (specific fields)

---

## Complete API Endpoint Summary

### User/Profile Operations
| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/profile/{userId}` | Get user profile |
| PUT | `/api/profile/{userId}` | Update full profile |
| PATCH | `/api/profile/{userId}/avatar` | Update avatar |

### Heart Rate Operations
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/heart-rates` | Create new record |
| GET | `/api/heart-rates/{userId}` | Get all records |
| GET | `/api/heart-rates/{userId}/latest` | Get latest record |
| GET | `/api/heart-rates/{userId}/take?limit=5` | Get last 5 records |
| GET | `/api/heart-rates/{userId}/stats` | Get statistics (avg, max, min, count) |
| PUT | `/api/heart-rates/{recordId}` | Update record |
| DELETE | `/api/heart-rates/{recordId}` | Delete record |

### Auth/OTP Operations (Already Existing)
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login user |
| POST | `/api/otp/request` | Request OTP |
| POST | `/api/otp/verify` | Verify OTP |

### Insight Operations (Already Existing)
| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/insight/summary/{userId}` | Get AI summary |

---

## Swagger Documentation

All new endpoints include Swagger JSDoc comments and will automatically appear in:
- **Swagger UI**: `http://localhost:5001/api/docs`
- **Swagger JSON**: `http://localhost:5001/api/swagger.json`

---

## Testing

See `API_TEST_COMMANDS.md` for comprehensive curl commands to test all endpoints.

### Quick Test Flow:
1. Register/Login to get `TOKEN` and `USER_ID`
2. Create multiple heart rate records
3. Test retrieval operations:
   ```bash
   # Get latest
   curl https://bpmboo-backend.onrender.com/api/heart-rates/USER_ID/latest \
     -H "Authorization: Bearer TOKEN"
   
   # Get last 5
   curl "https://bpmboo-backend.onrender.com/api/heart-rates/USER_ID/take?limit=5" \
     -H "Authorization: Bearer TOKEN"
   
   # Get stats
   curl https://bpmboo-backend.onrender.com/api/heart-rates/USER_ID/stats \
     -H "Authorization: Bearer TOKEN"
   ```
4. Test update/delete:
   ```bash
   # Update record
   curl -X PUT https://bpmboo-backend.onrender.com/api/heart-rates/RECORD_ID \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer TOKEN" \
     -d '{"bpmValue": 88, "feelingTag": "After exercise", "note": "Updated"}'
   
   # Delete record
   curl -X DELETE https://bpmboo-backend.onrender.com/api/heart-rates/RECORD_ID \
     -H "Authorization: Bearer TOKEN"
   ```

---

## Files Modified

1. `/backend/src/controllers/heartRateController.js` - Added 3 new functions
2. `/backend/src/controllers/profileController.js` - Added 1 new function
3. `/backend/src/routes/heartRateRoutes.js` - Added 3 new routes with Swagger docs
4. `/backend/src/routes/profileRoutes.js` - Added 1 new route with Swagger docs

No changes needed to:
- `src/server.js` (routes already mounted)
- `src/config/swagger.js` (JSDoc comments automatically picked up)
- Database models (all fields already exist)

---

## Next Steps

1. **Push to Git**:
   ```bash
   git add .
   git commit -m "feat: Add complete CRUD operations for Heart Rate and Profile endpoints"
   git push
   ```

2. **Deploy to Render**: Push will trigger automatic deployment

3. **Test on Production**:
   - Wait for Render deployment to complete
   - Visit: `https://bpmboo-backend.onrender.com/api/docs`
   - Test endpoints in Swagger UI
   - Or use curl commands from `API_TEST_COMMANDS.md`

4. **Verify Swagger Documentation**:
   - All new endpoints should appear in Swagger UI
   - Try them out directly in the browser
   - Check request/response schemas

---

## Error Handling

All endpoints follow consistent error response format:
```json
{
  "success": false,
  "message": "Error description"
}
```

Success responses:
```json
{
  "success": true,
  "message": "Optional success message",
  "data": { /* response data */ }
}
```

---

## Security Notes

- All endpoints (except Auth/OTP) require Bearer token authentication
- Token is verified via middleware
- Updates are scoped to resources (users can only update their own data if enforced at middleware level)
- Swagger docs are publicly accessible (consider restricting in production)

---

## Performance Considerations

- `/take?limit=5` uses `.limit()` to reduce database query size
- `/latest` uses `.findOne()` instead of `.find()` + `.sort()` for better performance
- `/stats` calculates averages in-memory (suitable for current usage, may need optimization if records grow)
- All queries use proper indexing on userId and timestamp
