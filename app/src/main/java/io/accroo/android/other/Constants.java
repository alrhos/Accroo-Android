package io.accroo.android.other;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by oscar on 25/10/17.
 */

public class Constants {
    public static final int MIN_PASSWORD_LENGTH = 10;
    public static final int MAX_PASSWORD_LENGTH = 4096;
    public static final String RECAPTCHA_SITE_KEY = "6LduSH4UAAAAAAcY15ThqAo9wJ0Lz3O6RKLW9ehm";
    public static final String ACCROO_SUPPORT_EMAIL = "support@accroo.io";
    public static final String FORGOT_PASSWORD_URL = "https://accroo.io/forgot-password";
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("dd MMM yyyy");
}
