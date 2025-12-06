# Permissions

## Required Permissions

The app requires the following permissions:

- `POST_NOTIFICATIONS`: Show todo notifications (Android 13+)
- `SCHEDULE_EXACT_ALARM`: Schedule exact alarm for due date reminders (Android 12+)
- `USE_EXACT_ALARM`: Use exact alarm API
- `WAKE_LOCK`: Wake device for notifications
- `RECEIVE_BOOT_COMPLETED`: Reschedule notifications after device restart
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`: Request exemption from battery optimization

## Setting Up Notifications

To ensure you receive notifications when your phone is idle or screen is off:

### 1. Grant Notification Permission (Android 13+)

**Automatic:**
- The app will request this on first launch

**Manual:**
- Go to: Settings → Apps → Todo App → Notifications → Allow

### 2. Enable Exact Alarms (Android 12+)

**Guided Setup:**
- The app will guide you to the settings

**Manual:**
- Go to: Settings → Apps → Todo App → Alarms & reminders → Allow
- This ensures notifications appear exactly when todos are due

### 3. Disable Battery Optimization

!!! warning "Critical for Pixel Phones"
    This is the **most important step** for Pixel phones and devices with aggressive battery management!

**Automatic:**
- The app will prompt you to exempt it from battery optimization

**Manual:**
- Settings → Apps → Todo App → Battery → Unrestricted

### 4. Additional Pixel-Specific Settings

For Google Pixel phones, additional steps may be needed:

1. **Battery Optimization**
   - Settings → Apps → Todo App → Battery → Battery optimization → Not optimized

2. **Adaptive Preferences**
   - Settings → Battery → Adaptive preferences → Disable for Todo App

3. **Special App Access**
   - Settings → Apps → Special app access → Battery optimization → Todo App → Don't optimize

## Troubleshooting Notifications

### Not Receiving Notifications When Phone Is Idle

1. ✅ Check that all three permissions above are granted
2. ✅ Verify notification settings: Settings → Notifications → App notifications → Todo App
3. ✅ Ensure "Do Not Disturb" mode isn't blocking notifications
4. ✅ Restart the app after granting battery optimization exemption
5. ✅ Create a test todo due in 2-3 minutes to verify notifications work

### Notifications Appear Late

- Check battery optimization settings
- Ensure "Alarms & reminders" permission is granted
- Restart device after changing settings

### No Notification Sound/Vibration

- Check notification channel settings
- Settings → Apps → Todo App → Notifications → Todo Reminders
- Ensure sound and vibration are enabled

### Notifications Don't Wake Screen

- Check that `WAKE_LOCK` permission is granted
- Some devices require additional settings to wake screen

## Testing Notifications

### Using the Test Notification Feature

1. Open the app
2. Menu → Test Notification
3. A notification should appear immediately
4. Swipe down to expand and see the "Mark Complete" button

This helps verify that:
- Notification permissions are working
- Notification channels are configured
- Notification actions work correctly

### Creating a Real Test

1. Add a new todo
2. Set due date/time to 2-3 minutes from now
3. Enable notifications
4. Save the todo
5. Lock your phone and wait
6. Notification should appear at the exact time

## Permission Best Practices

### When to Grant Permissions

- Grant notification permission to receive reminders
- Grant exact alarm permission for precise timing
- Disable battery optimization for reliable background notifications

### Privacy Considerations

All permissions are used exclusively for:
- Scheduling and displaying todo notifications
- Keeping the app alive in the background for timely alerts
- No data is collected or shared

## Platform-Specific Notes

### Android 13+ (API 33)
- Runtime notification permission required
- Must explicitly request `POST_NOTIFICATIONS`

### Android 12+ (API 31)
- Exact alarm permission required
- Must request `SCHEDULE_EXACT_ALARM`

### Android 7.0-11 (API 24-30)
- Battery optimization exemption recommended
- No runtime notification permission needed
