# BHTikTok++ for Android

LSPosed/Xposed модуль — порт iOS-твика **BHTikTok++** на Android. Патчит официальный TikTok (`com.zhiliaoapp.musically` / `com.ss.android.ugc.aweme`) в рантайме без модификации APK.

## Функционал (44 фичи iOS → Android)

| Категория | Фича | Описание |
|-----------|------|----------|
| **Скачивание** | `FEATURE_DOWNLOAD_BUTTON` | Кнопка ⬇ под каждым видео для загрузки |
| **Водяной знак** | `FEATURE_REMOVE_WATERMARK` | Удаляет watermark из URL видео |
| **Регион** | `FEATURE_UPLOAD_REGION` | Подмена страны (US, JP, RU и т.д.) |
| **Реклама** | `FEATURE_HIDE_ADS` | Скрывает рекламные посты в ленте |
| **UI** | `FEATURE_HIDE_EMOJI` | Скрывает Emoji-бар в комментариях |
| **UI** | `FEATURE_HIDE_TOP_ITEMS` | Скрывает верхний оверлей в ленте |
| **UI** | `FEATURE_PROGRESS_BAR` | Всегда показывает прогресс-бар видео |
| **UI** | `FEATURE_AUTO_PLAY` | Автопереход на следующее видео |
| **UI** | `FEATURE_DISABLE_LIVE` | Отключает прямые трансляции |
| **UI** | `FEATURE_DISABLE_SENSITIVE` | Пропускает предупреждения о контенте |
| **Фейк** | `FEATURE_FAKE_VERIFIED` | Галочка верификации на всех профилях |
| **Фейк** | `FEATURE_FAKE_FOLLOWER_COUNT` | 999,999 подписчиков |
| **Фейк** | `FEATURE_FAKE_FOLLOWING_COUNT` | 999,999 подписок |
| **Фейк** | `FEATURE_FAKE_LIKE_COUNT` | 999,999 лайков |
| **Подтверждения** | `FEATURE_CONFIRM_LIKE` | Диалог перед лайком |
| **Подтверждения** | `FEATURE_CONFIRM_FOLLOW` | Диалог перед подпиской |
| **Приватность** | `FEATURE_ANONYMOUS_SEEN` | Не отмечать сообщения прочитанными |

## Требования

- Android 8.0+ (API 26+)
- **Root** + **LSPosed Framework** (или Xposed)
- Официальный TikTok (любая версия, но стабильнее на не-обфусцированных)

## Установка

1. Собрать APK:
   ```bash
   ./gradlew assembleDebug
   ```
2. Установить APK на устройство
3. В LSPosed Manager активировать модуль **BHTikTok Android**
4. Выбрать scope: `TikTok` (`com.zhiliaoapp.musically` или `com.ss.android.ugc.aweme`)
5. Перезапустить TikTok
6. Открыть ярлык **BHTikTok Settings** на рабочем столе и включить нужные фичи

## Архитектура

```
app/src/main/java/com/bhtiktok/
├── BHTikTokModule.java          # Главный Xposed entry point
├── hooks/
│   ├── AdsHook.java             # Скрытие рекламы
│   ├── AnonymousSeenHook.java   # Анонимный просмотр сообщений
│   ├── AutoPlayHook.java        # Автовоспроизведение
│   ├── ConfirmHook.java         # Подтверждения действий
│   ├── DownloadHook.java        # Кнопка скачивания
│   ├── FakeChangesHook.java     # Фейковые счетчики
│   ├── ProgressBarHook.java     # Прогресс-бар
│   ├── RegionHook.java          # Смена региона
│   ├── UIHook.java              # Скрытие UI-элементов
│   └── WatermarkHook.java       # Удаление watermark
├── ui/
│   └── SettingsActivity.java    # Панель настроек
└── utils/
    ├── DownloadManager.java     # Загрузка файлов
    ├── PrefsHelper.java         # SharedPreferences
    └── VideoUrlExtractor.java   # Извлечение чистых URL
```

## Отладка через Frida

В папке `frida/` лежат скрипты для быстрой отладки без пересборки:

```bash
# Перечисление классов
frida -U -f com.zhiliaoapp.musically -l frida/enumerate_classes.js --no-pause

# Хук Aweme (URL + ads)
frida -U com.zhiliaoapp.musically -l frida/hook_aweme.js

# Хук User (fake changes)
frida -U com.zhiliaoapp.musically -l frida/hook_user.js
```

## Маппинг iOS → Android классов

| iOS (BHTikTok++) | Android (TikTok) | Пакет |
|------------------|------------------|-------|
| `AWEAwemeModel` | `Aweme` | `com.ss.android.ugc.aweme.feed.model` |
| `AWEUserModel` | `User` | `com.ss.android.ugc.aweme.profile.model` |
| `AWEURLModel` | `UrlModel` | `com.ss.android.ugc.aweme.base.model` |
| `AWENewFeedTableViewController` | `FeedFragment` | `com.ss.android.ugc.aweme.feed.ui` |
| `TTKProfileHomeViewController` | `ProfileFragment` | `com.ss.android.ugc.aweme.profile.ui` |
| `TTKCommentPanelViewController` | `CommentListFragment` | `com.ss.android.ugc.aweme.comment` |
| `TTKStoreRegionService` | `RegionService` | `com.ss.android.ugc.aweme.setting.country` |

## Anti-Detection

- В LSPosed включите **Скрытие** для TikTok
- Используйте **HideMyApplist** если SafetyNet срабатывает
- Модуль использует `MODE_WORLD_READABLE` SharedPreferences — безопасно для LSPosed

## Лицензия

Educational purposes only. Используйте на свой страх и риск.
