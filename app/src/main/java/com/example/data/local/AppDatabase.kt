package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.DemandPrediction
import com.example.data.model.FeedbackItem
import com.example.data.model.FoodItem
import com.example.data.model.NotificationItem
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.OrderStatus
import com.example.data.model.SalesRecord
import com.example.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        FoodItem::class,
        Order::class,
        OrderItem::class,
        NotificationItem::class,
        FeedbackItem::class,
        SalesRecord::class,
        DemandPrediction::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun canteenDao(): CanteenDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_canteen_db"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialDemoData(database.canteenDao())
                    }
                }
            }
        }

        suspend fun populateInitialDemoData(dao: CanteenDao) {
            // Seed Users
            val student = User(
                id = 1,
                studentId = "CS2026042",
                name = "Aarav Sharma",
                email = "aarav.sharma@college.edu",
                phone = "+91 98765 43210",
                role = "STUDENT",
                walletBalance = 420.0,
                department = "Computer Science & Engg (3rd Year)",
                profileAvatar = "👨‍🎓"
            )
            val admin = User(
                id = 2,
                studentId = "ADMIN01",
                name = "Chef Ramesh (Admin)",
                email = "canteen.admin@college.edu",
                phone = "+91 98765 00001",
                role = "ADMIN",
                walletBalance = 5000.0,
                department = "Central Canteen Operations",
                profileAvatar = "👨‍🍳"
            )
            dao.insertUser(student)
            dao.insertUser(admin)

            // Seed Food Items
            val foods = listOf(
                // Breakfast
                FoodItem(1, "Crispy Masala Dosa", "Golden brown crepe served with spiced potato masala, coconut chutney & hot sambar", "Breakfast", 50.0, 8, 280, true, true, 40, "🥞", 4.9f, 215, "Bestseller,Crispy"),
                FoodItem(2, "Idli Vada Combo", "2 Steamed fluffy idlis + 1 crispy medu vada with gun powder & fresh chutneys", "Breakfast", 45.0, 5, 210, true, true, 35, "🥟", 4.7f, 180, "Healthy,Fast"),
                FoodItem(3, "Indori Poha & Sev", "Light flattened rice tempered with mustard, curry leaves, crunchy peanuts & lemon", "Breakfast", 30.0, 4, 180, true, true, 25, "🥣", 4.6f, 140, "Light,Popular"),
                FoodItem(4, "Puri Bhaji (3 Pcs)", "Puffy golden puris served with aromatic spiced potato gravy & pickled onions", "Breakfast", 45.0, 10, 340, true, true, 20, "🫓", 4.5f, 95, "Fulfilling"),

                // Lunch
                FoodItem(5, "Executive South Indian Thali", "Rice, 2 rotis, paneer sabzi, dal tadka, curd, pickle, papad & gulab jamun", "Lunch", 95.0, 12, 580, true, true, 50, "🍱", 4.9f, 310, "Chef Choice,Value Meal"),
                FoodItem(6, "Hyderabadi Veg Dum Biryani", "Aromatic long grain basmati rice cooked with garden veggies & saffron spices", "Lunch", 85.0, 10, 490, true, true, 30, "🍚", 4.8f, 260, "Spicy,Popular"),
                FoodItem(7, "Paneer Butter Masala with 3 Rotis", "Rich creamy tomato gravy with soft cottage cheese cubes & hot butter rotis", "Lunch", 110.0, 14, 520, true, true, 25, "🍲", 4.8f, 195, "Rich,Special"),
                FoodItem(8, "Homestyle Rajma Chawal", "Slow simmered red kidney beans in Punjabi gravy over steamed jeera rice", "Lunch", 70.0, 8, 410, true, true, 30, "🍛", 4.7f, 175, "Comfort Food"),

                // Snacks
                FoodItem(9, "Crunchy Veg Samosa (2 pcs)", "Crispy pastry stuffed with spiced potatoes & peas, served with mint & tamarind dip", "Snacks", 25.0, 3, 260, true, true, 60, "🥟", 4.8f, 420, "Snack King,Quick"),
                FoodItem(10, "Double Cheese Veg Burger", "Crisp vegetable patty, melted cheddar cheese, fresh lettuce & signature herb mayo", "Snacks", 60.0, 10, 390, true, true, 30, "🍔", 4.7f, 280, "Cheesy,Hot"),
                FoodItem(11, "Peri Peri French Fries", "Golden crisp potato fries tossed in zesty African peri-peri spice blend", "Snacks", 50.0, 6, 290, true, true, 45, "🍟", 4.6f, 230, "Crunchy"),
                FoodItem(12, "Paneer Tikka Kathi Roll", "Flaky paratha wrap filled with tandoori spiced paneer cubes, capsicum & mint dressing", "Snacks", 70.0, 10, 340, true, true, 20, "🌯", 4.9f, 190, "Chef Special"),
                FoodItem(13, "Grilled Corn Cheese Sandwich", "Toasted jumbo bread loaded with sweet corn, jalapenos & gooey mozzarella", "Snacks", 55.0, 8, 310, true, true, 25, "🥪", 4.6f, 160, "Toasted"),

                // Drinks
                FoodItem(14, "Special Adrak Elaichi Chai", "Freshly brewed hot milk tea infused with crushed ginger and aromatic green cardamom", "Drinks", 15.0, 3, 75, true, true, 100, "☕", 4.9f, 540, "All-Time Fav"),
                FoodItem(15, "South Indian Filter Coffee", "Authentic chicory blend brewed decoction with frothy hot milk", "Drinks", 20.0, 3, 90, true, true, 80, "🧋", 4.8f, 320, "Aromatic"),
                FoodItem(16, "Thick Cold Coffee w/ Ice Cream", "Blended rich espresso with chilled milk, topped with vanilla scoop & chocolate syrup", "Drinks", 45.0, 5, 230, true, true, 40, "🥤", 4.9f, 290, "Refreshing"),
                FoodItem(17, "Fresh Mint Lemonade Soda", "Sparkling club soda with freshly squeezed lime, crushed mint leaves and black salt", "Drinks", 25.0, 2, 60, true, true, 50, "🍹", 4.7f, 180, "Cooler"),
                FoodItem(18, "Thick Alfonso Mango Lassi", "Sweet traditional yogurt smoothie blended with ripe alfonso mango pulp", "Drinks", 40.0, 4, 190, true, true, 30, "🥛", 4.8f, 150, "Sweet"),

                // Chef Specials
                FoodItem(19, "Mumbai Butter Pav Bhaji", "Spiced mashed vegetable curry garnished with butter, coriander & 2 toasted pavs", "Chef Specials", 75.0, 10, 460, true, true, 35, "🥘", 4.9f, 240, "Trending"),
                FoodItem(20, "Schezwan Veg Hakka Noodles", "Wok tossed noodles with crunchy bell peppers, cabbage and spicy garlic schezwan sauce", "Chef Specials", 80.0, 12, 380, true, true, 25, "🍜", 4.8f, 210, "Spicy")
            )
            dao.insertFoodItems(foods)

            // Seed Orders (Active + Historical)
            val now = System.currentTimeMillis()
            val order1 = Order(
                id = 1,
                orderNumber = "SC-8491",
                tokenNumber = "T-12",
                studentId = "CS2026042",
                studentName = "Aarav Sharma",
                studentPhone = "+91 98765 43210",
                pickupSlot = "12:30 PM - 12:45 PM",
                status = OrderStatus.READY.name,
                totalAmount = 145.0,
                paymentMethod = "UPI",
                paymentStatus = "PAID",
                qrPayload = "ORDER:SC-8491:T-12:CS2026042:145.0",
                createdAt = now - (20 * 60 * 1000),
                counterNumber = "Counter 1"
            )
            val order2 = Order(
                id = 2,
                orderNumber = "SC-8492",
                tokenNumber = "T-14",
                studentId = "ME2026019",
                studentName = "Priya Patel",
                studentPhone = "+91 98234 11223",
                pickupSlot = "12:45 PM - 01:00 PM",
                status = OrderStatus.PREPARING.name,
                totalAmount = 95.0,
                paymentMethod = "WALLET",
                paymentStatus = "PAID",
                qrPayload = "ORDER:SC-8492:T-14:ME2026019:95.0",
                createdAt = now - (12 * 60 * 1000),
                counterNumber = "Counter 2"
            )
            val order3 = Order(
                id = 3,
                orderNumber = "SC-8493",
                tokenNumber = "T-17",
                studentId = "EE2026088",
                studentName = "Rohan Verma",
                studentPhone = "+91 97654 33445",
                pickupSlot = "01:00 PM - 01:15 PM",
                status = OrderStatus.PENDING.name,
                totalAmount = 85.0,
                paymentMethod = "CASH",
                paymentStatus = "PAY_ON_PICKUP",
                qrPayload = "ORDER:SC-8493:T-17:EE2026088:85.0",
                createdAt = now - (4 * 60 * 1000),
                counterNumber = "Counter 2"
            )
            val order4 = Order(
                id = 4,
                orderNumber = "SC-8480",
                tokenNumber = "T-05",
                studentId = "CS2026042",
                studentName = "Aarav Sharma",
                studentPhone = "+91 98765 43210",
                pickupSlot = "09:15 AM - 09:30 AM",
                status = OrderStatus.COMPLETED.name,
                totalAmount = 95.0,
                paymentMethod = "UPI",
                paymentStatus = "PAID",
                qrPayload = "ORDER:SC-8480:T-05:CS2026042:95.0",
                createdAt = now - (5 * 60 * 60 * 1000),
                completedAt = now - (4 * 60 * 60 * 1000),
                rating = 5,
                feedbackNote = "Super hot masala dosa and quick counter pickup!",
                counterNumber = "Counter 1"
            )

            dao.insertOrders(listOf(order1, order2, order3, order4))

            // Seed Order Items
            val orderItems = listOf(
                OrderItem(orderId = 1, foodItemId = 1, foodName = "Crispy Masala Dosa", unitPrice = 50.0, quantity = 1, iconEmoji = "🥞", customizationNote = "Extra crispy please"),
                OrderItem(orderId = 1, foodItemId = 16, foodName = "Thick Cold Coffee w/ Ice Cream", unitPrice = 45.0, quantity = 1, iconEmoji = "🥤"),
                OrderItem(orderId = 1, foodItemId = 11, foodName = "Peri Peri French Fries", unitPrice = 50.0, quantity = 1, iconEmoji = "🍟"),

                OrderItem(orderId = 2, foodItemId = 5, foodName = "Executive South Indian Thali", unitPrice = 95.0, quantity = 1, iconEmoji = "🍱"),

                OrderItem(orderId = 3, foodItemId = 6, foodName = "Hyderabadi Veg Dum Biryani", unitPrice = 85.0, quantity = 1, iconEmoji = "🍚", customizationNote = "Less spicy"),

                OrderItem(orderId = 4, foodItemId = 1, foodName = "Crispy Masala Dosa", unitPrice = 50.0, quantity = 1, iconEmoji = "🥞"),
                OrderItem(orderId = 4, foodItemId = 16, foodName = "Thick Cold Coffee w/ Ice Cream", unitPrice = 45.0, quantity = 1, iconEmoji = "🥤")
            )
            dao.insertOrderItems(orderItems)

            // Seed Notifications
            val notifs = listOf(
                NotificationItem(
                    userId = "CS2026042",
                    title = "Order Ready for Pickup! 🔔",
                    message = "Your Token #T-12 (Order #SC-8491) is hot & ready at Counter 1. Please show your QR code to collect.",
                    orderId = 1,
                    type = "ORDER_UPDATE",
                    timestamp = now - (2 * 60 * 1000)
                ),
                NotificationItem(
                    userId = "CS2026042",
                    title = "Order Accepted by Kitchen 👨‍🍳",
                    message = "Chef Ramesh accepted your order #SC-8491. Estimated prep time: 10 mins.",
                    orderId = 1,
                    type = "ORDER_UPDATE",
                    timestamp = now - (15 * 60 * 1000)
                ),
                NotificationItem(
                    userId = "ALL",
                    title = "Expo Special Discount 🌟",
                    message = "Get ₹10 off on all South Indian Thalis during lunch hours (12 PM - 2 PM)!",
                    orderId = null,
                    type = "OFFER",
                    timestamp = now - (12 * 60 * 60 * 1000)
                )
            )
            notifs.forEach { dao.insertNotification(it) }

            // Seed Sales Records for Analytics (Weekly trend)
            val sales = listOf(
                SalesRecord(id = 1, dayName = "Mon", dateLabel = "Aug 18", totalRevenue = 7420.0, totalOrders = 124, peakHourLabel = "12:30 PM - 1:30 PM", topSellingFood = "Crispy Masala Dosa", wasteReductionKg = 12.4),
                SalesRecord(id = 2, dayName = "Tue", dateLabel = "Aug 19", totalRevenue = 8150.0, totalOrders = 142, peakHourLabel = "1:00 PM - 2:00 PM", topSellingFood = "South Indian Thali", wasteReductionKg = 14.8),
                SalesRecord(id = 3, dayName = "Wed", dateLabel = "Aug 20", totalRevenue = 9320.0, totalOrders = 168, peakHourLabel = "12:45 PM - 1:45 PM", topSellingFood = "Veg Dum Biryani", wasteReductionKg = 18.2),
                SalesRecord(id = 4, dayName = "Thu", dateLabel = "Aug 21", totalRevenue = 8640.0, totalOrders = 153, peakHourLabel = "1:00 PM - 2:00 PM", topSellingFood = "Crispy Masala Dosa", wasteReductionKg = 15.6),
                SalesRecord(id = 5, dayName = "Fri", dateLabel = "Aug 22", totalRevenue = 10450.0, totalOrders = 189, peakHourLabel = "12:30 PM - 1:30 PM", topSellingFood = "Double Cheese Burger", wasteReductionKg = 21.0),
                SalesRecord(id = 6, dayName = "Sat", dateLabel = "Aug 23", totalRevenue = 6800.0, totalOrders = 110, peakHourLabel = "4:00 PM - 5:30 PM", topSellingFood = "Adrak Chai & Samosa", wasteReductionKg = 11.5),
                SalesRecord(id = 7, dayName = "Today (Sun)", dateLabel = "Aug 24", totalRevenue = 5980.0, totalOrders = 98, peakHourLabel = "12:00 PM - 1:30 PM", topSellingFood = "Crispy Masala Dosa", wasteReductionKg = 16.3)
            )
            dao.insertSalesRecords(sales)

            // Seed AI Demand Predictions (The Expo Headline Feature)
            val predictions = listOf(
                DemandPrediction(
                    id = 1,
                    foodName = "Crispy Masala Dosa",
                    category = "Breakfast",
                    iconEmoji = "🥞",
                    predictedQuantity = 135,
                    historicalAverage = 115,
                    changePercentage = 17,
                    targetMealSlot = "Breakfast (8:00 AM - 10:30 AM)",
                    peakTimeWindow = "8:45 AM - 9:30 AM",
                    confidenceScore = 96,
                    recommendation = "High morning rush expected. Prepare 15-20% extra potato masala batter before 8 AM to avoid counter queuing.",
                    wasteRiskStatus = "LOW"
                ),
                DemandPrediction(
                    id = 2,
                    foodName = "Special Adrak Elaichi Chai",
                    category = "Drinks",
                    iconEmoji = "☕",
                    predictedQuantity = 210,
                    historicalAverage = 180,
                    changePercentage = 16,
                    targetMealSlot = "Evening Snacks (4:00 PM - 5:30 PM)",
                    peakTimeWindow = "4:15 PM - 5:00 PM",
                    confidenceScore = 98,
                    recommendation = "Peak student recess at 4:30 PM. Keep 2 large continuous thermal dispensers ready by 4 PM.",
                    wasteRiskStatus = "LOW"
                ),
                DemandPrediction(
                    id = 3,
                    foodName = "Executive South Indian Thali",
                    category = "Lunch",
                    iconEmoji = "🍱",
                    predictedQuantity = 95,
                    historicalAverage = 80,
                    changePercentage = 18,
                    targetMealSlot = "Lunch (12:30 PM - 2:00 PM)",
                    peakTimeWindow = "12:45 PM - 1:30 PM",
                    confidenceScore = 93,
                    recommendation = "Predicted demand surge due to CS & Mech lab schedule break at 1 PM. Pre-pack 40 thalis.",
                    wasteRiskStatus = "LOW"
                ),
                DemandPrediction(
                    id = 4,
                    foodName = "Crunchy Veg Samosa",
                    category = "Snacks",
                    iconEmoji = "🥟",
                    predictedQuantity = 160,
                    historicalAverage = 145,
                    changePercentage = 10,
                    targetMealSlot = "Evening Snacks (3:30 PM - 5:30 PM)",
                    peakTimeWindow = "4:00 PM - 4:45 PM",
                    confidenceScore = 95,
                    recommendation = "Batch fry 80 pieces at 3:45 PM and second batch of 80 pieces at 4:25 PM for maximum crispiness.",
                    wasteRiskStatus = "LOW"
                ),
                DemandPrediction(
                    id = 5,
                    foodName = "Hyderabadi Veg Dum Biryani",
                    category = "Lunch",
                    iconEmoji = "🍚",
                    predictedQuantity = 75,
                    historicalAverage = 90,
                    changePercentage = -16,
                    targetMealSlot = "Lunch (12:00 PM - 2:00 PM)",
                    peakTimeWindow = "1:00 PM - 1:45 PM",
                    confidenceScore = 91,
                    recommendation = "Slight reduction suggested compared to last Friday; restrict handi to 75 portions to prevent evening leftover wastage.",
                    wasteRiskStatus = "MODERATE"
                ),
                DemandPrediction(
                    id = 6,
                    foodName = "Thick Cold Coffee",
                    category = "Drinks",
                    iconEmoji = "🥤",
                    predictedQuantity = 88,
                    historicalAverage = 70,
                    changePercentage = 25,
                    targetMealSlot = "All Day (11:00 AM - 5:00 PM)",
                    peakTimeWindow = "1:15 PM - 2:00 PM & 4:30 PM",
                    confidenceScore = 94,
                    recommendation = "Sunny weather forecast (32°C). Stock extra ice-cream tubs and pre-brew cold decoction.",
                    wasteRiskStatus = "SURGE_EXPECTED"
                )
            )
            dao.insertPredictions(predictions)
        }
    }
}
