package com.example.util

enum class AppLanguage {
    ARABIC,
    ENGLISH
}

object Strings {
    fun get(key: String, lang: AppLanguage): String {
        val entry = dictionary[key] ?: return key
        return when (lang) {
            AppLanguage.ARABIC -> entry.ar
            AppLanguage.ENGLISH -> entry.en
        }
    }

    private data class LocalizedPair(val ar: String, val en: String)

    private val dictionary = mapOf(
        "app_title" to LocalizedPair("فاير ولف", "Fire Wolf"),
        "nav_dashboard" to LocalizedPair("الرئيسية", "Dashboard"),
        "nav_organizer" to LocalizedPair("الألعاب والرموز", "Games & Codes"),
        "nav_booster" to LocalizedPair("المعزز", "Booster"),
        "nav_challenges" to LocalizedPair("التحديات", "Challenges"),
        "nav_profile" to LocalizedPair("الملف الشخصي", "Profile"),

        // Dashboard
        "dash_hero_tag" to LocalizedPair("مركز الألعاب الاحترافي", "Pro Gaming Hub"),
        "dash_quick_boost" to LocalizedPair("تعزيز فائق الآن", "Quick Turbo Boost"),
        "dash_system_status" to LocalizedPair("حالة النظام والأداء", "System & Performance"),
        "dash_ram_usage" to LocalizedPair("استهلاك الذاكرة RAM", "RAM Memory Usage"),
        "dash_ping_status" to LocalizedPair("زمن الاستجابة (بينج)", "Network Ping Latency"),
        "dash_fps_status" to LocalizedPair("معدل الإطارات", "Current Frame Rate"),
        "dash_temp_status" to LocalizedPair("حرارة المعالج", "Device Temperature"),
        "dash_active_game" to LocalizedPair("اللعبة النشطة الحالية", "Current Active Game"),
        "dash_cloud_server" to LocalizedPair("خادم المزامنة الخاص", "Dedicated Cloud Server"),
        "dash_cloud_secure" to LocalizedPair("اتصال مشفر AES-256 وحماية ضد الاحتيال", "Encrypted AES-256 & Anti-Tamper"),
        "dash_vip_badge" to LocalizedPair("عضوية VIP", "VIP Member"),
        "dash_free_badge" to LocalizedPair("حساب مجاني", "Standard Player"),
        "dash_upgrade_vip" to LocalizedPair("ترقية للاشتراك المميز", "Upgrade to VIP"),

        // Game Organizer & Codes
        "org_tab_games" to LocalizedPair("ألعابي", "My Games"),
        "org_tab_codes" to LocalizedPair("نقل الرموز والأكواد", "Transfer Codes"),
        "org_tab_accounts" to LocalizedPair("إدارة الحسابات", "Game Accounts"),
        "org_add_game" to LocalizedPair("إضافة لعبة", "Add Game"),
        "org_transfer_code_btn" to LocalizedPair("نقل أو استيراد رمز", "Transfer / Import Code"),
        "org_add_account_btn" to LocalizedPair("إضافة حساب لعبة", "Add Account"),
        "org_code_verified" to LocalizedPair("تم التحقق بتوقيع الحماية", "Anti-Fraud Signature Verified"),
        "org_code_copy" to LocalizedPair("نسخ الرمز", "Copy Code"),
        "org_code_share" to LocalizedPair("مشاركة الرمز", "Share Code"),
        "org_qr_transfer" to LocalizedPair("نقل عبر رمز QR", "QR Code Transfer"),
        "org_fraud_check" to LocalizedPair("فحص الحماية من الاختراق", "Security Hash Check"),
        "org_switch_account" to LocalizedPair("تفعيل الحساب", "Switch Account"),
        "org_active_slot" to LocalizedPair("الحساب النشط", "Active Slot"),
        "org_enter_code_hint" to LocalizedPair("أدخل رمز اللعبة أو الإعدادات...", "Enter game or config code..."),
        "org_code_type_redeem" to LocalizedPair("كود استرداد مكافأة", "Redeem Gift Code"),
        "org_code_type_config" to LocalizedPair("إعدادات 120 FPS فائقة", "120 FPS Ultra Config"),
        "org_code_type_sens" to LocalizedPair("مصفوفة الحساسية", "Pro Sensitivity Matrix"),
        "org_code_type_room" to LocalizedPair("مفتاح غرفة بطولة", "Tournament Room Key"),

        // Booster
        "boost_title" to LocalizedPair("معزز الألعاب فائق السرعة", "Ultra Game Booster"),
        "boost_subtitle" to LocalizedPair("تحرير الذاكرة وتثبيت معدل الإطارات وتقليل البينج", "Flush memory, stabilize FPS, drop latency"),
        "boost_start_btn" to LocalizedPair("تفعيل التيربو BOOST", "ACTIVATE TURBO BOOST"),
        "boost_running" to LocalizedPair("جاري تحسين المعالج والذاكرة...", "Optimizing GPU & Clearing RAM..."),
        "boost_completed" to LocalizedPair("تم تعزيز الأداء بنجاح!", "Performance Boosted Successfully!"),
        "boost_mode_title" to LocalizedPair("وضع الألعاب المختار", "Gaming Performance Mode"),
        "boost_mode_normal" to LocalizedPair("وضع متوازن", "Balanced"),
        "boost_mode_perf" to LocalizedPair("أداء عالي", "High Performance"),
        "boost_mode_ultra" to LocalizedPair("تيربو سايبر 120Hz", "Ultra Cyber 120Hz"),
        "boost_mode_battery" to LocalizedPair("توفير الطاقة", "Battery Saver"),
        "boost_dnd_toggle" to LocalizedPair("حظر الإشعارات أثناء اللعب (DND)", "Do Not Disturb Gaming Mode"),
        "boost_ping_test" to LocalizedPair("فحص زمن استجابة الخادم", "Live Ping Latency Test"),
        "boost_ping_good" to LocalizedPair("ممتاز للمنافسة", "Optimal for Esports"),

        // Challenges & Events
        "chall_title" to LocalizedPair("التحديات والفعاليات", "Challenges & Events"),
        "chall_daily" to LocalizedPair("التحديات اليومية", "Daily Challenges"),
        "chall_weekly" to LocalizedPair("التحديات الأسبوعية", "Weekly Events"),
        "chall_claim" to LocalizedPair("استلام المكافأة", "Claim Reward"),
        "chall_claimed" to LocalizedPair("تم الاستلام", "Claimed"),
        "chall_in_progress" to LocalizedPair("قيد الإنجاز", "In Progress"),
        "chall_admin_btn" to LocalizedPair("لوحة المشرف", "Admin Controls"),
        "chall_create_title" to LocalizedPair("إضافة تحدي جديد", "Create Challenge"),
        "chall_admin_desc" to LocalizedPair("واجهة مخصصة للمشرفين لإنشاء فعاليات جديدة ومكافآت", "Supervisor interface to broadcast events & rewards"),
        "chall_add_btn" to LocalizedPair("نشر التحدي", "Publish Challenge"),

        // Profile & Customization
        "prof_title" to LocalizedPair("الملف الشخصي للاعب", "Player Profile"),
        "prof_edit" to LocalizedPair("تخصيص الملف", "Customize Profile"),
        "prof_display_name" to LocalizedPair("اسم العرض", "Display Name"),
        "prof_bio" to LocalizedPair("النبذة التعريفية", "Player Bio"),
        "prof_gamer_tag" to LocalizedPair("رمز اللاعب ID", "Gamer Tag ID"),
        "prof_level" to LocalizedPair("المستوى", "Level"),
        "prof_coins" to LocalizedPair("عملات GameCoins", "GameCoins"),
        "prof_cosmetics" to LocalizedPair("الإطارات والعناصر التجميلية", "Cosmetic Avatar Frames"),
        "prof_unlocked" to LocalizedPair("مفتوح", "Unlocked"),
        "prof_locked" to LocalizedPair("مقفل - يتطلب مستوى", "Locked - Requires Level"),
        "prof_save" to LocalizedPair("حفظ التغييرات", "Save Changes"),
        "prof_google_linked" to LocalizedPair("متصل بحساب Google Play", "Linked with Google Play"),
        "prof_google_btn" to LocalizedPair("تسجيل الدخول بجوجل", "Sign in with Google"),

        // Subscriptions
        "sub_title" to LocalizedPair("اشتراكات فاير ولف VIP", "Fire Wolf VIP Subscriptions"),
        "sub_desc" to LocalizedPair("افتح إمكانيات غير محدودة وأداء 120Hz مع خوادم فائقة السرعة", "Unlock unlimited power, 120Hz boosting & dedicated servers"),
        "sub_plan_monthly" to LocalizedPair("الاشتراك الاحترافي الشهري", "Pro Gamer Monthly"),
        "sub_plan_yearly" to LocalizedPair("الاشتراك الذهبي السنوي VIP", "Elite VIP Annual Pass"),
        "sub_subscribe_btn" to LocalizedPair("اشترك الآن بأمان", "Subscribe Securely"),
        "sub_cancel_anytime" to LocalizedPair("يمكنك الإلغاء في أي وقت من متجر Google Play", "Cancel anytime via Google Play Store"),

        // Notifications
        "notif_title" to LocalizedPair("مركز الإشعارات الذكية", "Smart Notification Center"),
        "notif_mark_all" to LocalizedPair("تحديد الكل كمقروء", "Mark all read"),
        "notif_empty" to LocalizedPair("لا توجد إشعارات جديدة", "No new notifications"),

        // Play Policy & Security
        "policy_title" to LocalizedPair("الأمان وسياسة الخصوصية", "Security & Privacy Policy"),
        "policy_verified" to LocalizedPair("متوافق تماماً مع إرشادات متجر Google Play", "100% Google Play Developer Policy Compliant"),
        "policy_text" to LocalizedPair("يحترم التطبيق خصوصيتك ولا يجمع أي بيانات حساسة، وجميع الأكواد مشفرة بتوقيع SHA-256.", "Fire Wolf respects your privacy and uses zero-permission storage with encrypted SHA-256 signatures.")
    )
}
