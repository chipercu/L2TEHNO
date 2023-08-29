package l2open.config;

import java.nio.ByteOrder;
import java.util.*;

import l2open.gameserver.model.L2Zone.ZoneType;

/**
 * @author : Diagod
 * @date : 12.06.12
 */
public class ConfigValue {

	public static boolean develop = Boolean.parseBoolean(System.getenv("DEVELOP"));
    public static int GeoWaterZ_Diff = 0;
    public static int GeoTest = 1;
    public static int GeoTestDeltaZ = 512;
    public static int GeoTestDiffZ = 128;
    /*************************** advipsystem.properties ***************************/
    public static String NetMask2 = "10.0.0.0/255.255.255.0,10.0.1.0/255.255.255.0";
    public static String NetMask1 = "192.168.5.0/255.255.255.0,192.168.0.0/255.255.255.0";
    public static String IPAdress2 = "10.0.0.10";
    public static String IPAdress1 = "192.168.5.31";

    /*************************** ai.properties ***************************/
    public static boolean AlwaysTeleportHome = false;
    public static int UseSkillDelay = 20000;
    public static boolean AltTeleportingToma = false;
    public static boolean BlockActiveTasks = false;
    public static int MonsterChanceUseUltimateDefence = 5;
    public static int MaxPursueUndergroundRange = 2000;
    public static int RndWalkRate = 1;
    public static boolean RunnableLog = true;
    public static int RndAnimationRate = 2;
    public static int AiTaskMangerCount = 1;
    public static int UseSkillChance = 35;
    public static int AggroCheckInterval = 250;
    public static boolean MonstersLooters = false;
    public static boolean SayCastingSkillName = false;
    public static int RespawnTimeRaidBossMinion = 120000;
    public static int AiTaskDelay = 250;
    public static int MaxPursueRangeRaid = 5000;
    public static boolean AltAiKeltirs = false;
    public static int AiTaskActiveDelay = 250;
    public static int MaxPursueRange = 4000;
    public static int MonstersWeaponEnchantChance = 0;
    public static int MonstersWeaponEnchantMin = 0;
    public static int MonstersWeaponEnchantMax = 0;
    public static int MaxDriftRange = 100;
    public static boolean RndWalk = true;
    public static int MutatedElpyCount = 27;
    public static boolean BelethNeedCommandChanel = true;
    public static int BelethManagerCount = 36;
    public static long MinAdenaLakfiEat = 10000;
    public static long TimeIfNotFeedDissapear = 10;
    public static long IntervalBetweenEating = 15;
    public static String ADebugServerPacketsChar = "";
    public static String ADebugClientPacketsChar = "";
    public static boolean AllNpcInvull = false;
    public static boolean VortexOneBoss = false;
    public static int VortexBossTime = 60;
    public static byte LairReproductionMin = 5;
    public static byte LairReproductionMax = 5;
    public static double LairReproductionChance = 15;
    public static double VortexBossChance25718 = 14.25;
    public static double VortexBossChance25719 = 14.25;
    public static double VortexBossChance25720 = 14.25;
    public static double VortexBossChance25721 = 14.25;
    public static double VortexBossChance25722 = 14.25;
    public static double VortexBossChance25723 = 14.25;
    public static double VortexBossChance25724 = 14.25;
    public static int BelethBugPunishment = 1;
    public static boolean AdeptSayText = false;
    public static boolean NpcCallOneBoss = false;
    public static int NpcCallBossTime = 60;
    public static int NpcCallChance50007 = 60;
    public static int NpcCallChance50008 = 60;
    public static int NpcCallChance50009 = 60;

    /*************************** altsettings.properties ***************************/
    public static boolean AltPartyMaker= true;
    public static int MyTeleportsMaxSlot = 9;
    public static boolean RemoveAutoShot = false;
    public static float RateSiegeGuardsPrice = 1f;
    public static boolean AugmentAll = false;
    public static int[] ShopPriceLimits = {};
    public static int[] ShopUnallowedItems = {};
    public static double AltChampionChance2 = 0.;
    public static int AltPcBangPointsDelay = 20;
    public static double AltChampionChance1 = 0.;
    public static int RecruitFC = 18;
    public static int CaptainFC = 27;
    public static int AugmentationMidGlowChance = 40;
    public static boolean AllowFakePlayers = false;
    public static int AugmentationHighSkillChance = 45;
    public static boolean AutoLootHerbs = false;
    public static int NonOwnerItemPickupDelay = 15;
    public static float RateExpSpForCraft = 1f;
    public static boolean AllowShiftClick = true;
    public static int AugmentationMidSkillChance = 30;
    public static boolean AllowNobleTPToAll = false;
    public static float[] AltPartyBonus = {1.00f, 1.10f, 1.20f, 1.30f, 1.40f, 1.50f, 2.00f, 2.10f, 2.20f};
    public static boolean AllowChDoorOpenOnClick = true;
    public static boolean AltVitalityEnabled = true;
    public static int AltCatacombMonstersMultHP = 4;
    public static int AltMaxLevel = 85;
    public static int AugmentationTopSkillChance = 60;
    public static boolean AltAllowSubClassWithoutBaium = true;
    public static boolean AltGenerateDroplistOnDemand = true;
    public static boolean AltChampionSocial = false;
    public static double CraftMasterworkLevelMod = 0.2;
    public static boolean TeleToCatacombs = false;
    public static int AugmentationTopGlowChance = 100;
    public static int RiftMinPartySize = 2;
    public static int AltPartyDistributionRange = 1500;
    public static boolean AutoLootIndividual = false;
    public static int AltAddRecipes = 0;
    public static int MammonUpgrade = 6680500;
    public static boolean AllowDropAugmented = false;
    public static float AltVitalityConsumption = 1f;
    public static boolean AltAllowClanCommandOnlyForClanLeader = true;
    public static boolean AllowTalkWhileSitting = false;
    public static boolean checkWeaponPenalty = true;
    public static boolean AltAllowSubClassWithoutQuest = false;
    public static float MaxLoadModifier = 1.0f;
    public static boolean Delevel = true;
    public static boolean AutoLootFromRaids = false;
    public static double FestivalRatePrice = 1.0;
    public static int MammonExchange = 26968400;
    public static float GkCostMultiplier = 1.0f;
    public static boolean AltKarmaPlayerCanShop = false;
    public static boolean AltChampionAggro = false;
    public static boolean ArenaExp = true;
    public static int FakePlayersPercent = 100;
    public static boolean SellReenterAbyssTicket = false;
    public static double AltPcBangPointsDoubleChance = 10.;
    public static int GkFree = 40;
    public static int AugmentationBaseStatChance = 1;
    public static int FestivalMinPartySize = 5;
    public static float AltCatacombMonstersRespawn = 1.0f;
    public static boolean AltShowDroplist = true;
    public static String KamalokaLimit = "All";
    public static int AutoJumpsDelay = 8;
    public static int AltPcBangPointsBonus = 0;
    public static boolean SellReenterNightmaresTicket = false;
    public static boolean AutoLootPK = false;
    public static boolean AltRequireClanCastle = false;
    public static int ChampionMaxLevel = 70;
    public static boolean WearTestEnabled = false;
    public static boolean AltFullStatsPage = true;
    public static boolean SiegeOperateDoors = false;
    public static int CommanderFC = 30;
    public static boolean checkArmorPenalty = true;
    public static int ChampionMinLevel = 20;
    public static boolean AltAllowOthersWithdrawFromClanWarehouse = false;
    public static int[] ChampionRewardItemQtyMin = {1, 1};
    public static int[] ChampionRewardItemQtyMax = {1, 1};
    public static int[] ChampionRewardFame = {0, 0};
    public static boolean Alt100PercentRecipesS80 = false;
    public static int AugmentationAccSkillChance = 10;
    public static int AugmentationStatChance = 0;
    public static boolean PartyLeaderOnlyCanInvite = true;
    public static boolean SellReenterLabyrinthTicket = false;
    public static int AltMaxAllySize = 3;
    public static float AltVitalityPower = 1.0f;
    public static int FollowRange = 100;
    public static boolean BSCrystallize = false;
    public static int ChampionRewardLowerLvlItemChance = 0;
    public static boolean AutoLoot = false;
    public static boolean AutoLootPA = false;
    public static int GkCruma = 56;
    public static int OfficerFC = 24;
    public static double CraftMasterworkChance = 3.;
    public static boolean AllowTattoo = false;
    public static int SoldierFC = 21;
    public static boolean AllowShadowWeapons = true;
    public static boolean AltChSimpleDialog = false;
    public static boolean EnableFishingWaterCheck = true;
    public static boolean AltUnregisterRecipe = false;
    public static int[] CanNotChampionsId = {};
    public static int AugmentationNGGlowChance = 0;
    public static boolean AltAllowAdenaDawn = true;
    public static boolean CraftMasterworkChest = false;
    public static int AltMaxSubLevel = 80;
    public static int BuffMaxLevel = 75;
    public static boolean Alt100PercentRecipesS = false;
    public static int AutoJumpsDelayRandom = 120000;
    public static boolean DontAllowPetsOnSiege = false;
    public static boolean AltPcBangPointsEnabled = false;
    public static int BuffMinLevel = 6;
    public static boolean AltRequireCastleDawn = true;
    public static int AltLevelToGetSubclass = 75;
    public static boolean KillCounter = false;
    public static int[] CanChampionsId = {};
    public static int AugmentationHighGlowChance = 70;
    public static boolean AltMatherialsDrop = false;
    public static boolean KamalokaNightmaresPremiumOnly = false;
    public static boolean Alt100PercentRecipesB = false;
    public static boolean Alt100PercentRecipesA = false;
    public static int AltSubAdd = 0;
    public static int RiftSpawnDelay = 10000;
    public static boolean AltSocialActionReuse = false;
    public static boolean CraftCounter = false;
    public static boolean NoLasthitOnRaid = false;
    public static int ChampionRewardHigherLvlItemChance = 0;
    public static float AltRaidRespawnMultiplier = 1f;
    public static boolean AltExpForCraft = false;
    public static int AugmentationNGSkillChance = 15;
    public static boolean PushkinSignsOptions = false;
    public static int ShutdownMsgType = 3;
    public static boolean ImprovedPetsLimitedUse = false;
    public static boolean AltChAllBuffs = false;
    public static int AltVitalityRaidBonus = 1000;
    public static int ChampionAdenasRewards = 1;
    public static boolean AltChAllowHourBuff = false;
    public static int HeroFC = 33;
    public static float ChampionRewards = 8;
    public static int ChampionRewardItemID = 57;
    public static int SoulCrystalRate = 5;
    public static int TrueChests = 50;
    public static int SSAnnouncePeriod = 0;
    public static boolean DropCounter = false;
    public static boolean PetsHealOnlyInBattle = true;
    public static boolean AllowSellCommon = true;
    public static int AltPcBangPointsMinLvl = 1;
    public static boolean SiegeOperateDoorsLordOnly = true;
    public static int[] DisabledMultisells = {};
    public static int SiegeFameOnKillMin = 10;
    public static int SiegeFameOnKillMax = 20;
    public static int SetAllPrice = -1;
    public static boolean ShowClanCrestWithoutQuest = false;
    public static boolean SevenSignsAll = false;
    public static float ItemPraceMod = 1;
    public static int VitaminPetRegenItemId = -2;
    public static int VitaminPetHungryPercent = 1;
    public static int VitaminPetRegenValue = 12;
    public static int VitaminPetRegenItemCount = 1;
    public static int AltItemAuctionItemId = 57;
    public static long AltItemAuctionBitLimit = 100000000000L;
    public static int KmAllToMeReuse = 10;

    /*************************** CCPService.properties ***************************/
    public static String SendOnlineColor = "FFFFFF";
    public static long SendOnlineTimeResend = 5000;
    public static int SendOnlineY = 10;
    public static int SendOnlineType = 3;
    public static int SendOnlineX = 250;
    public static int SendOnlineFakeMy = 0;
    public static String SendServerTitleColor = "FFFFFF";
    public static long SendServerTitleTimeResend = 0;
    public static int SendServerTitleY = 30;
    public static int SendServerTitleX = 250;
    public static String SendServerTitle = "Text";

    /*************************** communityboard.properties ***************************/
    public static boolean GiveAllSkillsForClassUp = false;
    public static boolean pvpBoardBuffer = false;
    public static int LevelFreeTP = 0;
    public static boolean AllowCBBufferOnEvent = false;
    public static int OneBuffPrice = 1000;
    public static int NamePerRowOnCommunityBoard = 5;
    public static int NamePageSizeOnCommunityBoard = 50;
    public static boolean AllowCBInAbnormalState = false;
    public static int[] CBEnchantLvl = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
    public static int[] CBEnchantPrice = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
    public static int[] CBEnchantArmorLvl = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
    public static int[] CBEnchantArmorPrice = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
    public static boolean AllowCBBufferOnSiege = false;
    public static int CommBufferMinLvl = 20;
    public static boolean CommunityBoardSortPlayersList = true;
    public static boolean AllowCBEnchant = false;
    public static String AllowCommunityBoardPlayersList = "all";
    public static int[] CBLvlUp = {10, 20, 30, 40, 50, 60, 70, 80, 85};
    public static int CBTeleportPrice = 5000;
    public static int GroupBuffPriceModifier = 3;
    public static int CBLvlUpItem = 4037;
    public static int CBEnchantItem = 4356;
    public static long CBTeleportSavePrice = 5000;
    public static boolean restoreBuff = false;
    public static boolean AllowCommunityBoard = true;
    public static int DanceAndSongTime = 300000;
    public static int CommunityBoardPlayersListCache = 60;
    public static int LevelFreeBuff = 0;
    public static int CommBufferMaxLvl = 85;
    public static boolean AllowCBTeleport = false;
    public static boolean AllowCBClassMaster = false;
    public static int[] CBLvlUpPrice = {10, 20, 30, 40, 50, 60, 70, 80, 85};
    public static int[] CBEnchantAtributeLvl = {25, 50, 75, 100, 125, 150};
    public static int[] CBEnchantAtributePrice = {25, 50, 75, 100, 125, 150};
    public static int[] CBEnchantArmorAtributeLvl = {12, 24, 48, 96, 108, 120};
    public static int[] CBEnchantArmorAtributePrice = {12, 24, 48, 96, 108, 120};
    public static int[] CBHeroItem = {4356};
    public static int CBMaxEnchant = 25;
    public static int BuffTime = 1200000;
    public static int[] CBHeroItemPrice = {1000};
    public static int maxBuffSchem = 10;
    public static boolean ALlowCBBufferInInstance = false;
    public static boolean AllowCBTInAbnormalState = false; // Разрешить использование Телепорта(КБ) в "ненормальных" состояниях (типа в pvp, мертвым и т.д.)
    public static boolean AllowCBCInAbnormalState = false; // Разрешить использование Класс Мастера(КБ) в "ненормальных" состояниях (типа в pvp, мертвым и т.д.)
    public static boolean AllowCBMInAbnormalState = false; // Разрешить использование Магазина(КБ) в "ненормальных" состояниях (типа в pvp, мертвым и т.д.)
    public static boolean AllowCBSInAbnormalState = false; // Разрешить использование Сервисов(КБ) и т.д. в "ненормальных" состояниях (типа в pvp, мертвым и т.д.)
    public static boolean AllowCBBInAbnormalState = false; // Разрешить использование Бафера(КБ) и т.д. в "ненормальных" состояниях (типа в pvp, мертвым и т.д.)
    public static boolean OnlyCBTeleportPeace = false; // Разрешить телепортацию ТОЛЬКО из мирных зон(список в файле game\data\zone\peace_zone.xml)?
    public static int[] CBHeroTime = {1};
    public static boolean OnlineCheatEnable = false;
    public static int OnlineCheatType = 0;
    public static boolean OnlineCheatPercentEnable = false;
    public static int OnlineCheatPercent = 50;
    public static int OnlineCheatCount = 5000;
    public static boolean OfflineCheatEnable = false;
    public static boolean OfflineCheatPercentEnable = false;
    public static int OfflineCheatPercent = 50;
    public static int OfflineCheatCount = 5000;
    public static int OnlineRefresh = 10;
    public static int OfflineRefresh = 10;

    /*************************** cb_terms.properties ***************************/
    public static boolean CheckOutOfTown = false;
    public static boolean CheckInMoveDisabled = false;
    public static boolean CheckInCombat = false;
    public static boolean CheckInDeath = false;
    public static boolean CheckInSiege = false;
    public static boolean CheckInAttack = false;
    public static boolean CheckInOlympiad = false;
    public static boolean CheckInFly = false;
    public static boolean CheckInDuel = false;
    public static boolean CheckInInstance = false;
    public static boolean CheckInJail = false;
    public static boolean CheckInOutOfControl = false;
    public static boolean CheckInEvent = false;

    /*************************** cb_global.properties ***************************/
    public static String BBSDefault = "_bbshome";
    public static String BBSFavorites = "_bbsgetfav";
    public static String BBSWebsite = "_bbslink";
    public static String BBSRegion = "_bbsloc";
    public static String BBSClan = "_bbsclan";
    public static String BBSMemo = "_bbsmemo";
    public static String BBSMail = "_maillist_0_1_0_";
    public static String BBSFriendList = "_friendlist_0_";
    public static String CommunityBoardHtmlRoot = "data/html/CommunityBoard/";
    public static String Copyright = "Powered by L2CCCP && Open-Team.ru";
    public static boolean FunnyError = false;
    public static String[] CommunityBoardReplaceHtm = {};

    /*************************** cb_buffer.properties ***************************/
    public static boolean AllowBuffer = false;
    public static int BufferItem = 57;
    public static int BufferPriceOne = 1000;
    public static int BufferSaveItem = 57;
    public static int BufferSaveMax = 5;
    public static int BufferSavePrice = 1000;
    public static int BufferTime = 1;
    public static int BufferMinLevel = 1;
    public static int BufferMaxLevel = 85;
    public static int BufferFreeLevel = 40;
    public static int[] BufferBuffs = {};
    public static int[] BufferOnlyPaBuffs = {};
    public static int[] BufferRecovery = {1, 1, 1, 1};
    public static boolean BufferClear = false;
    public static boolean BufferInInstance = false;
    public static boolean BufferInCombat = false;
    public static boolean BufferOnSiege = false;
    public static boolean BufferInWater = false;
    public static boolean BufferOnlyPeace = false;
    public static boolean BufferAffterRes = false;
    public static int BufferUsePremiumItem = -1;
    public static int BufferAffterResTime = 0;
    public static int BufferPremiumItem = 57;
    public static int BufferPremiumPriceOne = 10;
    public static int[] BufferBuffsPremium = {};
    public static int[] BufferBuffs2Premium = {};

    /*************************** cb_statistic.properties ***************************/
    public static boolean StatsAllow = true;
    public static int StatsUpdate = 5;
    public static boolean StatisticAllow = true;
    public static int StatisticUpdateCount = 60;
    public static int StatisticUpdateTopPK = 60;
    public static int StatisticUpdateTopPVP = 60;
    public static int StatisticUpdateTopOnline = 60;
    public static int StatisticUpdateTopRich = 60;
    public static int StatisticUpdateTopClan = 60;
    public static int StatisticUpdateEpicBoss = 60;
    public static int StatisticUpdateOlyStat = 60;
    public static int StatisticUpdateOlyStat2 = 60;
    public static int StatisticCount = 10;
    public static int StatisticTopItem = 57;
    public static int[] StatisticRaid = {29068, 29020, 29028, 29062, 29065, 29186, 29001, 29006, 29014, 25701};
    public static boolean StatisticShowTime = true;
    public static boolean StatisticShowDate = true;
    public static boolean StatisticShowRespawn = true;

    /*************************** cb_news.properties ***************************/
    public static boolean NewsAllow = true;
    public static int NewsUpdate = 5;
    public static int NewsCount = 3;

    /*************************** cb_carrer.properties ***************************/
    public static boolean CarrerAllow = true;

    public static long[][] CarrerFirstItemPrice = {};
    public static long[][] CarrerSecondItemPrice = {};
    public static long[][] CarrerThirdItemPrice = {};

    public static int[] CarrerList = {0, 0, 0};
    public static int[] CarrerItem = {57, 57, 57};
    public static int[] CarrerPrice = {10000, 20000, 30000};
    public static int[] CarrerSecondItem = {};
    public static int[] CarrerSecondPrice = {};

    public static boolean CarrerBuyNobless = false;

    public static boolean CarrerSubAdd = false;
    public static int CarrerSubAddItem = 57;
    public static long CarrerSubAddPrice = 0;

    public static boolean CarrerSubChange = false;
    public static int CarrerSubChangeItem = 57;
    public static long CarrerSubChangePrice = 0;

    public static boolean CarrerSubCancel = false;
    public static int CarrerSubCancelItem = 57;
    public static long CarrerSubCancelPrice = 0;

    /*************************** cb_teleport.properties ***************************/
    public static int TeleportPrice = 25000;
    public static int TeleportItem = 57;
    public static int TeleportFreeLevel = 40;
    public static boolean TeleportPremiumFree = false;
    public static boolean TeleportPointOnlyPremium = false;
    public static boolean TeleportPointPremiumFree = false;
    public static int TeleportMaxPoint = 5;
    public static int TeleportSavePrice = 5000;
    public static int TeleportSaveItem = 57;
    public static boolean TeleportInInstance = false;
    public static boolean TeleportInCombat = false;
    public static boolean TeleportInPvpFlag = false;
    public static boolean TeleportOnSiege = false;
    public static boolean TeleportInWater = false;
    public static boolean TeleportPointOnlyStaticZone = false;
    public static String TeleportForbiddenZonesSave = "RESIDENCE, ssq_zone, battle_zone, Siege, no_restart, no_summon";
    public static String TeleportForbiddenZonesTp = "RESIDENCE, ssq_zone, battle_zone, Siege, no_restart, no_summon";
    public static int[] TeleportNoForbiddenZonesTp = {-1};
    public static int[] TeleportNoForbiddenZonesSave = {-1};

    /*************************** cb_lottery.properties ***************************/
    public static boolean SLotteryAllow = false;
    public static int SLotteryItem = 57;
    public static int[] SLotteryBet = new int[]{1000, 4000, 50000, 250000, 1000000, 5000000};
    public static int[] SLotteryLevel = new int[]{1, 85};
    public static double SLotteryWinChance = 35.5;
    public static double SLotteryJackpotChance = 0.23;
    public static int SLotteryRewardMul = 4;
    public static int SLotteryToJacktop = 1;
    public static int SLotteryNullJacktop = 1;
    public static int SLotteryMaxJacktop = 2147483647;

    public static boolean PLotteryAllow = false;
    public static boolean PLotteryOnlyPremium = false;
    public static int PLotteryItem = 57;
    public static int[] PLotteryBet = new int[]{1, 4, 10, 25, 50, 100};
    public static int[] PLotteryLevel = new int[]{1, 85};
    public static double PLotteryWinChance = 35.5;
    public static double PLotteryJackpotChance = 0.23;
    public static int PLotteryRewardMul = 4;
    public static int PLotteryToJacktop = 1;
    public static int PLotteryNullJacktop = 1;
    public static int PLotteryMaxJacktop = 100000;

    /*************************** developer.properties ***************************/
    public static boolean DebugClientPackets = false;
    public static boolean DebugMovePackets = false;
    public static boolean DEBUG_FREYA = false;
    public static boolean DEBUG_FRINTEZZA = false;
    public static boolean DebugServerPackets = false;
    public static boolean GCbreak = false;
    public static int[] NotMobSpawnId = {};
    public static boolean GoodsInventoryEnabled = false;
    public static boolean DeadlockDetectorKill = false;
    public static boolean AntharasDebug = false;
    public static boolean DebugClientPacketsPer = false;
    public static boolean DebugServerPacketsPer = false;
    public static int[] NotSpawnMob = {};
    public static String NotSeeServerPackets;
    public static String NotSeeClientPackets;
    public static int relation = 0;
    public static boolean enableDebugGsLs = false;
    public static boolean deleteFastAccessChar = false;
    public static long lastAccessTime = 0;
    public static int delLevel = 0;
    public static long GarbageCollectorDelay = 3600000;
    public static boolean GarbageCollectorShowOnline = true;
    public static boolean GarbageCollectorShowFullStat = true;
    public static boolean ShowNpcCastSkill = false;
    public static int MAX_SIZE = 9;
    public static int[] OnlySendPetItem = {0};
    public static boolean CanAutoAtackPvp = false;
    public static boolean CanAutoAtackPvpOnBow = false;
    public static boolean UseOldDebuffFormula = false;
    public static double CapCurseOfDivinity = 3.449999999999996;
    public static boolean TestActionFail = false;
    public static boolean TestAtackFail = false;
    public static boolean ThreadPoolManagerDebug = false;
    public static int ThreadPoolManagerDebugInterval = 1000;
    public static int ThreadPoolManagerDebugDeflect = 0;
    public static boolean ThreadPoolManagerDebugLogConsol = true;
    public static boolean ThreadPoolManagerDebugLogFile = true;
    public static int ThreadPoolManagerDebugLogConsolDelay = 0;

    public static boolean SelectorThreadDebug = false;
    public static int SelectorThreadDebugInterval = 0;
    public static int DumpForNpc = 0;
    public static int NaiaRoomControllerDebug = 0;
    public static int NaiaRoomControllerDebug2 = 0;
    public static boolean RunnableStatsWrapper = false;
    public static int npc_dead = 100;
    public static int npc_alive = 100;
    public static boolean KamaelEquipAllItem = false;
    public static boolean DebugParam_setImobilised = false;
    public static boolean DebugParam_Sleeping = false;
    public static boolean DebugParam_Invul = false;
    public static boolean DebugParam_p_block_move = false;
    public static boolean DebugAtomicState = false;

    /*************************** epic.properties ***************************/
    public static int RandomIntervalOfAantaras = 0;
    public static int FixintervalOfValakas = 950400000;
    public static int RandomIntervalOfBaium = 28800000;
    public static int RandomIntervalOfValakas = 0;
    public static int FixIntervalOfBaium = 432000000;
    public static int FixIntervalOfAntharas = 950400000;
    public static int[] FixRespHourOfAntharas = {};
    public static int[] FixRespHourOfBaium = {};
    public static int[] FixRespHourOfValakas = {};
    public static int FixRespDayOfWeekAntharas = -1;
    public static int FixRespDayOfWeekBaium = -1;
    public static int FixRespDayOfWeekValakas = -1;
    public static int NeadLevelToZeken = 1;
    public static int MaxMinionOfAntharas = 60;
    public static int AntharasWaitingToSpawn = 30;
    public static int AntharasWaitingToSleep = 15;
    public static int MaxMinionOfValakas = 60;
    public static byte AQHighCharPunishment = 0;
    public static int MinChannelMembersAntharas = 99;
    public static int MinChannelMembersValakas = 99;
    public static int MinChannelMembersBaium = 54;
    public static int MinChannelMembersAntQueen = 36;
    public static int MinChannelMembersCore = 36;
    public static int MinChannelMembersOrfen = 36;
    public static int MinChannelMembersZaken = 36;
    public static int MinChannelMembersOtherRaidBoss = 18;
    public static boolean FreyaOneEnter = false;
    public static boolean ZakenOneEnter = false;
    public static boolean FrintezzaOneEnter = false;
    public static boolean SetFlagForEpicZone = false;
    public static int AddRndRespHourOfAntharas = 0;
    public static int AddRndRespHourOfBaium = 0;
    public static int AddRndRespHourOfValakas = 0;

    public static int FreyaMinPlayers = 9;
    public static int FreyaHardMinPlayers = 9;
    public static int ZakenMinPlayers = 9;
    public static int ZakenNightMinPlayers = 9;
    public static int ZakenHardMinPlayers = 9;
    public static int FrintezzaMinPlayers = 9;


    /*************************** MonsterAtack.properties ***************************/
    public static int TMWave1Count = 3;
    public static int TMEventInterval = 0;
    public static int TMWave2Count = 2;
    public static int[] TMItemChanceBoss = {50, 40, 50, 50, 50, 50, 50, 50, 20, 20};
    public static int TMStartMin = 00;
    public static int TMWave3Count = 2;
    public static long[] TMItemColBoss = {5, 777000000, 10, 10, 10, 10, 10, 10, 2, 2};
    public static int TMMobLife = 600000;
    public static int TMWave4Count = 2;
    public static int TMWave6 = 25699;
    public static int TMStartHour = 19;
    public static int TMWave5 = 18855;
    public static int TMWave4 = 18855;
    public static int TMWave5Count = 2;
    public static int TMWave3 = 25699;
    public static int TMWave2 = 18855;
    public static int TMBoss = 25700;
    public static int TMWave1 = 18855;
    public static int BossLifeTime = 1500000;
    public static int TMWave6Count = 2;
    public static int[] TMItemChance = {20, 70, 10, 10, 10, 10, 10, 10, 20, 20};
    public static int[] TMItem = {4037, 57, 9552, 9553, 9554, 9555, 9556, 9557, 6577, 6578};
    public static boolean TMEnabled = false;
    public static int TMTime6 = 300000;
    public static int TMTime5 = 300000;
    public static int TMTime4 = 300000;
    public static int TMTime3 = 300000;
    public static int TMTime2 = 300000;
    public static int TMTime1 = 120000;
    public static int[] TMItemCol = {1, 77700000, 1, 1, 1, 1, 1, 1, 1, 1};

    /*************************** LastHero.properties ***************************/
    public static int[] LastHeroStartTime = {00, 00, 03, 00, 06, 00, 9, 00, 12, 00, 15, 00, 18, 00, 21, 00};
    public static int[] LastHeroForbiddenItems = {57};
    public static boolean LastHeroIP = true;
    public static boolean LastHeroHWID = false;
    public static boolean LastHeroCategories = false;
    public static boolean LastHeroWinMaxDamager = false;
    public static int LastHeroPolymorphId = -1;
    public static int LastHeroTime = 3;
    public static int LastHeroEndTime = 300;
    public static int LastHeroBonusID = 57;
    public static float LastHeroBonusCount = 5000f;
    public static boolean LastHeroRate = true;
    public static float LastHeroFinalBonus = 10000f;
    public static boolean LastHeroFinalRate = true;
    public static boolean LastHeroCancel = false;
    public static boolean LastHeroSetHero = false;
    public static boolean LastHeroBuff = false;
    public static int LastHeroDanceAndSongTime = 300;
    public static int LastHeroBuffTime = 300;
    public static boolean LastHeroOlympiadItems = false;
    public static int[][] LastHeroMagicBuff = {{264, 1}, {267, 1}, {268, 1}, {273, 1}, {276, 1}, {304, 1}, {349, 1}, {363, 1}, {365, 1}, {529, 1}, {530, 1}, {1035, 1}, {1078, 1}, {1085, 1}, {1259, 1}, {1303, 1}, {1307, 1}, {1352, 1}, {1364, 1}, {1389, 1}, {1397, 1}, {1413, 1}, {1461, 1}, {1500, 1}, {1501, 1}, {1503, 1}, {1504, 1}, {1517, 1}, {4703, 1}};
    public static int[][] LastHeroPhisicBuff = {{264, 1}, {267, 1}, {268, 1}, {269, 1}, {271, 1}, {274, 1}, {275, 1}, {304, 1}, {310, 1}, {349, 1}, {364, 1}, {1035, 1}, {1036, 1}, {1259, 1}, {1307, 1}, {1352, 1}, {1357, 1}, {1364, 1}, {1388, 1}, {1397, 1}, {1460, 1}, {1461, 1}, {1501, 1}, {1502, 1}, {1504, 1}, {1517, 1}, {1519, 1}, {4699, 1}};
    public static String LastHeroName = "Враг";
    public static String LastHeroTitle = "Last Hero";
    public static int LastHeroArmor = 1;
    public static boolean LastHeroChangeName = false;
    public static boolean LastHeroNoParty = false;
    public static int[] LastHeroMinLevelForCategory = {20, 30, 40, 52, 62, 76};
    public static int[] LastHeroMaxLevelForCategory = {29, 39, 51, 61, 75, 85};
    public static boolean LastHeroBattleUseBuffer = false;

    /*************************** CaptureTheFlag.properties ***************************/
    public static int[] CaptureTheFlagStartTime = {01, 00, 04, 00, 07, 00, 10, 00, 13, 00, 16, 00, 19, 00, 22, 00};
    public static int[] CaptureTheFlagForbiddenItems = {57};
    public static boolean CaptureTheFlagIP = true;
    public static boolean CaptureTheFlagHWID = false;
    public static boolean CaptureTheFlagCategories = false;
    public static int CaptureTheFlagTime = 3;
    public static int CaptureTheFlagBonusID = 57;
    public static double CaptureTheFlagBonusCount = 5000.;
    public static boolean CaptureTheFlagRate = true;
    public static boolean CaptureTheFlagCancel = false;
    public static boolean CaptureTheFlagAddItemDraw = true;
    public static boolean CaptureTheFlagBuff = false;
    public static int CaptureTheFlagDanceAndSongTime = 300;
    public static int CaptureTheFlagBuffTime = 300;
    public static boolean CaptureTheFlagOlympiadItems = false;
    public static int[][] CaptureTheFlagMagicBuff = {{264, 1}, {267, 1}, {268, 1}, {273, 1}, {276, 1}, {304, 1}, {349, 1}, {363, 1}, {365, 1}, {529, 1}, {530, 1}, {1035, 1}, {1078, 1}, {1085, 1}, {1259, 1}, {1303, 1}, {1307, 1}, {1352, 1}, {1364, 1}, {1389, 1}, {1397, 1}, {1413, 1}, {1461, 1}, {1500, 1}, {1501, 1}, {1503, 1}, {1504, 1}, {1517, 1}, {4703, 1}};
    public static int[][] CaptureTheFlagPhisicBuff = {{264, 1}, {267, 1}, {268, 1}, {269, 1}, {271, 1}, {274, 1}, {275, 1}, {304, 1}, {310, 1}, {349, 1}, {364, 1}, {1035, 1}, {1036, 1}, {1259, 1}, {1307, 1}, {1352, 1}, {1357, 1}, {1364, 1}, {1388, 1}, {1397, 1}, {1460, 1}, {1461, 1}, {1501, 1}, {1502, 1}, {1504, 1}, {1517, 1}, {1519, 1}, {4699, 1}};
    public static boolean CaptureTheFlagNoParty = false;
    public static int[] CaptureTheFlagMinLevelForCategory = {20, 30, 40, 52, 62, 76};
    public static int[] CaptureTheFlagMaxLevelForCategory = {29, 39, 51, 61, 75, 85};
    public static boolean CaptureTheFlagBattleUseBuffer = false;
    public static boolean CaptureTheFlagSub = false;

    /*************************** SiegeCastle.properties ***************************/
    public static int[] SiegeCastleStartTime = {01, 00, 04, 00, 07, 00, 10, 00, 13, 00, 16, 00, 19, 00, 22, 00};
    public static int[] SiegeCastleForbiddenItems = {57};
    public static long[] SiegeCastleRewardWiner = {57, 1000};
    public static long[] SiegeCastleRewardTaker = {57, 10};
    public static int SiegeCastleTime = 5;
    public static int SiegeCastlePrepareBattleTime = 5;
    public static int SiegeCastleBattleTime = 60;
    public static boolean SiegeCastleOlympiadItems = false;

    /*************************** TeamvsTeam.properties ***************************/
    public static int[] TeamvsTeamStartTime = {02, 00, 05, 00, 8, 00, 11, 00, 14, 00, 17, 00, 20, 00, 23, 00};
    public static int[] TeamvsTeamForbiddenItems = {57};
    public static boolean TeamvsTeamIP = true;
    public static boolean TeamvsTeamHWID = false;
    public static boolean TeamvsTeamCategories = false;
    public static int TeamvsTeamTime = 3;
    public static int TeamvsTeamBonusID = 57;
    public static float TeamvsTeamBonusCount = 5000f;
    public static boolean TeamvsTeamRate = true;
    public static boolean TeamvsTeamCancel = false;
    public static boolean TeamvsTeamCancelGo = false;
    public static boolean TeamvsTeamBuff = false;
    public static int TeamvsTeamDanceAndSongTime = 300;
    public static int TeamvsTeamBuffTime = 300;
    public static int TeamvsTeamBattleTime = 5;
    public static boolean TeamvsTeamOlympiadItems = false;
    public static boolean TeamvsTeamBattleCount = false;
    public static boolean TeamvsTeamBattleUseBuffer = false;
    public static int[][] TeamvsTeamMagicBuff = {{264, 1}, {267, 1}, {268, 1}, {273, 1}, {276, 1}, {304, 1}, {349, 1}, {363, 1}, {365, 1}, {529, 1}, {530, 1}, {1035, 1}, {1078, 1}, {1085, 1}, {1259, 1}, {1303, 1}, {1307, 1}, {1352, 1}, {1364, 1}, {1389, 1}, {1397, 1}, {1413, 1}, {1461, 1}, {1500, 1}, {1501, 1}, {1503, 1}, {1504, 1}, {1517, 1}, {4703, 1}};
    public static int[][] TeamvsTeamPhisicBuff = {{264, 1}, {267, 1}, {268, 1}, {269, 1}, {271, 1}, {274, 1}, {275, 1}, {304, 1}, {310, 1}, {349, 1}, {364, 1}, {1035, 1}, {1036, 1}, {1259, 1}, {1307, 1}, {1352, 1}, {1357, 1}, {1364, 1}, {1388, 1}, {1397, 1}, {1460, 1}, {1461, 1}, {1501, 1}, {1502, 1}, {1504, 1}, {1517, 1}, {1519, 1}, {4699, 1}};
    public static int[] TeamvsTeamMinLevelForCategory = {20, 30, 40, 52, 62, 76};
    public static int[] TeamvsTeamMaxLevelForCategory = {29, 39, 51, 61, 75, 85};
    public static boolean TeamvsTeamSub = false;

    /*************************** CubicLoh.properties ***************************/
    public static int[] CubicLohStartTime = {-1};
    public static boolean CubicLohIP = true;
    public static boolean CubicLohHWID = false;
    public static int CubicLohRegTime = 5;
    public static int CubicLohMatchTime = 5;
    public static int CubicLohMaxPlayerCount = 50;
    public static int CubicLohRewardId = 4037;
    public static long CubicLohRewardCount = 10;
    public static float CubicLohBoxModiferCount = 1.5f;
    public static float CubicLohBoxModiferCountAdd = 0.5f;
    public static int[] CubicLohForbiddenItems = {57};
    public static boolean CubicLohOlympiadItems = false;

    /*************************** FractionEvent1.properties ***************************/
    public static int[] FractionEvent1StartTime = {02, 00, 05, 00, 8, 00, 11, 00, 14, 00, 17, 00, 20, 00, 23, 00};
    public static int[] FractionEvent1ForbiddenItems = {57};
    public static boolean FractionEvent1IP = true;
    public static boolean FractionEvent1HWID = false;
    public static int FractionEvent1Time = 3;
    public static boolean FractionEvent1Cancel = false;
    public static boolean FractionEvent1Buff = false;
    public static int FractionEvent1BattleTime = 5;
    public static boolean FractionEvent1OlympiadItems = false;
    public static boolean FractionEvent1Debug = false;
    public static long[] FractionEvent1RewardWiner = {57, 1000};

    /*************************** FractionEvent2.properties ***************************/
    public static int[] FractionEvent2StartTime = {02, 00, 05, 00, 8, 00, 11, 00, 14, 00, 17, 00, 20, 00, 23, 00};
    public static int[] FractionEvent2ForbiddenItems = {57};
    public static boolean FractionEvent2IP = true;
    public static boolean FractionEvent2HWID = false;
    public static int FractionEvent2Time = 3;
    public static boolean FractionEvent2Cancel = false;
    public static boolean FractionEvent2Buff = false;
    public static int FractionEvent2BattleTime = 5;
    public static boolean FractionEvent2OlympiadItems = false;
    public static boolean FractionEvent2Debug = false;
    public static long[] FractionEvent2RewardWiner = {57, 1000};

    /*************************** FractionEvent3.properties ***************************/
    public static int[] FractionEvent3StartTime = {02, 00, 05, 00, 8, 00, 11, 00, 14, 00, 17, 00, 20, 00, 23, 00};
    public static int[] FractionEvent3ForbiddenItems = {57};
    public static boolean FractionEvent3IP = true;
    public static boolean FractionEvent3HWID = false;
    public static int FractionEvent3Time = 3;
    public static boolean FractionEvent3Cancel = false;
    public static boolean FractionEvent3Buff = false;
    public static int FractionEvent3BattleTime = 5;
    public static boolean FractionEvent3OlympiadItems = false;
    public static boolean FractionEvent3Debug = false;
    public static long[] FractionEvent3RewardWiner = {57, 1000};

    /*************************** FractionEvent4.properties ***************************/
    public static int[] FractionEvent4StartTime = {02, 00, 05, 00, 8, 00, 11, 00, 14, 00, 17, 00, 20, 00, 23, 00};
    public static int[] FractionEvent4ForbiddenItems = {57};
    public static boolean FractionEvent4IP = true;
    public static boolean FractionEvent4HWID = false;
    public static int FractionEvent4Time = 3;
    public static boolean FractionEvent4Cancel = false;
    public static boolean FractionEvent4Buff = false;
    public static int FractionEvent4BattleTime = 5;
    public static boolean FractionEvent4OlympiadItems = false;
    public static boolean FractionEvent4Debug = false;
    public static long[] FractionEvent4RewardWiner = {57, 1000};

    /*************************** events.properties ***************************/
    public static float TFH_POLLEN_CHANCE = 20f;
    public static boolean BountyHuntersEnabled = true;
    ;
    public static float March8DropChance = 10f;
    public static int EVENTBUFFER_CHANT_ITEM_ID = 57;
    public static boolean EVENTBUFFER_ENABLE_SKILL_GETHITTIME = true;
    public static int EVENTBUFFER_DANCE_ITEM_ID = 57;
    public static int EVENTBUFFER_ALLDANCEOFSONGS_ITEM_COUNT = 1000000;
    public static int EVENTBUFFER_MAX_LVL = 86;
    public static int MouseBaseItemAfterRB = 40;
    public static float CofferOfShadowsPriceRate = 1f;
    public static boolean EVENTBUFFER_ENABLE_PET_BUFF = true;
    public static int EVENTBUFFER_WARRIORDANCEOFSONGS_ITEM_COUNT = 500000;
    public static float TREASURE_SACK_CHANCE = 10f;
    public static int RabbitsToRichesScrolPrice = 500;
    public static int RabbitsToRichesScrolBuyTime = 12;
    public static int SavingSnowmanLoteryPrice = 50000;
    public static int EVENTBUFFER_MIN_LVL = 1;
    public static int PriceEnchantMasterId = 57;
    public static float L2DAY_LETTER_CHANCE = 1f;
    public static boolean EVENTBUFFER_ENABLE_BUFF_ANIMATION = true;
    public static float RabbitsToRichesRewardRate = 1f;
    public static boolean EnchMasterUseAdenaRates = true;
    public static int SavingSnowmanRewarderChance = 2;
    public static float EVENT_CHANGE_OF_HEART_CHANCE = 5f;
    public static long EnchMaster1ScrollPrice = 77777;
    public static int MouseItemCount = 4;
    public static long EnchMaster24ScrollPrice = 6000;
    public static float MEDAL_CHANCE = 10f;
    public static int EVENTBUFFER_SONG_ITEM_COUNT = 2500;
    public static int MouseItemChanche = 100;
    public static long EnchMasterStaffPrice = 10000;
    public static int EVENTBUFFER_OTHER_ITEM_COUNT = 2500;
    public static int EVENTBUFFER_MAGEDANCEOFSONGS_ITEM_ID = 57;
    public static int EVENTBUFFER_HEAL_ITEM_COUNT = 50000;
    public static boolean EVENTBUFFER_SPAWN_EVENT_NPC = true;
    public static float March8PriceRate = 1f;
    public static float CofferOfShadowsRewardRate = 1f;
    public static float TRICK_OF_TRANS_CHANCE = 10f;
    public static int EVENTBUFFER_DANCE_ITEM_COUNT = 2500;
    public static int EVENTBUFFER_ALLDANCEOFSONGS_ITEM_ID = 57;
    public static int EVENTBUFFER_PACK_ITEM_ID = 57;
    public static int EVENTBUFFER_MAGEDANCEOFSONGS_ITEM_COUNT = 500000;
    public static int EVENTBUFFER_HEAL_ITEM_ID = 57;
    public static int EVENTBUFFER_WARRIORDANCEOFSONGS_ITEM_ID = 57;
    public static int MouseItemId = 10639;
    public static float EnchMasterDropChance = 1f;
    public static int EVENTBUFFER_CHANT_ITEM_COUNT = 2500;
    public static float GLITTMEDAL_CHANCE = 0.1f;
    public static int EVENTBUFFER_PACK_ITEM_COUNT = 100000;
    public static int EVENTBUFFER_OTHER_ITEM_ID = 57;
    public static int EVENTBUFFER_SONG_ITEM_ID = 57;

    /*************************** simple.properties ***************************/
    public static double ESimpleChance = 50.;
    public static int[] ESimple = {};
    public static int ESimpleMinCount = 1;
    public static int ESimpleMaxCount = 4;
    public static int ESimpleMinLevel = 76;
    public static int ESimpleMaxLevel = 85;
    public static double ESimpleRbChance = 10.;
    public static int[] ESimpleRb = {};
    public static int ESimpleRbMinCount = 10;
    public static int ESimpleRbMaxCount = 40;
    public static int ESimpleRbMinLevel = 76;
    public static int ESimpleRbMaxLevel = 85;
    public static int ESimpleManager = 36618;
    public static boolean ESimpleRateHp = true;
    public static boolean ESimpleMessage = true;

    /*************************** RewardofHonor.properties ***************************/
    public static int ERewardofHonorManager = 36619;
    public static int[] ERewardofHonorCords = {110248, -87688, -3294};
    public static int[] ERewardofHonorMonster = {22789, 22790, 22791, 22793};
    public static int ERewardofHonorKills = 500;
    public static int[][] ERewardofHonorReward = {{13432}, {13433}, {13434}};
    public static boolean ERewardofHonorMessage = true;

    /*************************** geodata.properties ***************************/
    public static int MinLayerHeight = 64;
    public static int GeoLastY = 26;
    public static int GeoLastX = 26;
    public static int PathClean = 1;
    public static boolean AllowMoveWithKeyboard = true;
    public static int PathFindBoost = 1;
    public static int MaxZDiff = 64;
    public static long PathFindMaxTime = 100000000;
    public static int ClientZShift = 16;
    public static int GeoWaterZ = 6;
    public static boolean GeodataDebug = false;
    public static boolean GeodataDebugWaterMove = false;
    public static boolean GeodataDebugWaterMoveNLos = false;
    public static boolean PathfindDebug = false;
    public static boolean AllowDoors = true;
    public static boolean SimplePathFindForMobs = true;
    public static boolean CompactGeoData = false;
    public static boolean GeodataEnabled = true;
    public static int GeoFirstY = 10;
    public static int GeoFirstX = 11;
    public static double Weight2 = 1.;
    public static double Weight1 = 2.;
    public static boolean PathFindDiagonal = true;
    public static double Weight0 = 0.5;
    public static boolean AllowFallFromWalls = false;
    public static String PathFindBuffers = "8x96;8x128;8x160;8x192;8x256;4x288;4x320;2x384;1x512";
    public static int PathFindMaxZDiff = 32;
    public static String GeoFilesPattern = "(\\d{2}_\\d{2})\\.l2j";
    public static boolean DelayedSpawn = false;
    public static String GeodataRoot = "./";
    public static int MaxZDiffEdge = 128;
    public static int PathFindMapMul = 2;

    public static int ViewOffset = 1;
    public static int DivBy = 2048;
    public static int DivByForZ = 1024;

    /*************************** l2open-version.properties ***************************/
    public static String version = "Unknown Version";
    public static String builddate = "Undefined Date.";

    /*************************** olympiad.properties ***************************/
    public static boolean OlympiadRemoveAutoShot = true;
    public static long AltOlyWPeriod = 604800000;
    public static int MaxCompForAll = 70;
    public static int AltOlyClassedRewItemCount = 50;
    public static int AltOlyVPeriod = 43200000;
    public static int AltOlyCompRewItem = 13722;
    public static int AltOlyRandomTeamRewItemCount = 30;
    public static int RandomTeamGameMin = 5;
    public static int AltOlyNonClassedRewItemCount = 40;
    public static int MaxCompForNonClassed = 60;
    public static int AltOlyGPPerPoint = 1000;
    public static int AltOlyRank5Points = 30;
    public static int TeamGameMin = 5;
    public static int AltOlyRank4Points = 40;
    public static long AltOlyCPeriod = 21600000;
    public static int ClassGameMin = 10;
    public static int AltOlyHeroPoints = 200;
    public static boolean Olympiad_HWID = false;
    public static int MaxCompForTeam = 10;
    public static int AltOlyMin = 00;
    public static int OldTypeSort = 2;
    public static int MaxCompForClassed = 30;
    public static int StartBattle = 60;
    public static int AltOlyRank1Points = 100;
    public static boolean EnableOlympiadSpectating = true;
    public static boolean Olympiad_HWID_LOG = false;
    public static int NonClassGameMin = 10;
    public static int AltOlyBattleRewItem = 13722;
    public static int AltOlyBattleLoosItem = 0;
    public static long AltOlyBattleLoosItemCount = 0;
    public static int OlyWeanTeamAddItemId = 0;
    public static long OlyWeanTeamAddItemCount = 0;
    public static int AltOlyStartTime = 18;
    public static int AltFakeOlyStartTime = -1;
    public static int AltFakeOlyEndTime = -1;
    public static boolean EnableOlympiad = true;
    public static boolean EnableEffectIconOlympiad = true;
    public static int MaxPointLoose = 10;
    public static int OlympiadStadiasCount = 160;
    public static int TleportToArena = 120;
    public static int AltOlyTeamRewItemCount = 80;
    public static int AltOlyRank3Points = 55;
    public static int AltOlyRank2Points = 75;
    public static int OlympiadEndMonthAdd = 1;
    public static int OlympiadEndWeekAdd = 0;
    public static int OlympiadEndDayOfWeekSet = 2;
    public static int OlympiadGivePointToCrash = 0;
    public static boolean MaxEnchantForOlympiadEnable = false;
    public static int MaxEnchantWeaponForOlympiad = 65536;
    public static int MaxEnchantArmorForOlympiad = 65536;
    public static int MaxEnchantJewelForOlympiad = 65536;
    public static int OlympiadSetStartPoint = 10;
    public static int[] OlympiadEndDateList = {};
    public static int[] OlympiadWeeklyPeriodDateList = {};
    public static int OlympiadAltRewardChance = -1;
    public static boolean OlympiadTakePointForCrash = true;

    public static boolean EnableRewardForLoser = false;
    public static int RewardIdForLoser = 57;
    public static int RewardCountForLoser = 1000;

    /*************************** Enchant.properties ***************************/
    public static int EnchantMaxMasterYogi = 23;
    public static int EnchantMaxWeapon = 20;
    public static int EnchantChanceOlfs = 50;
    public static int EnchantChanceOlfsBlessed = 50;
    public static int EnchantChanceCrystal = 68;
    public static int EnchantChanceArmor = 52;
    public static int MaxEnchantLevelOlf = 10;
    public static int EnchantChanceCrystalAccessory = 54;
    public static int EnchantChanceAccessory = 54;
    public static int EnchantChance = 68;
    public static int EnchantAttributeCrystalChance = 30;
    public static int EnchantChanceMasterYogi = 68;
    /**
     * # По пиздачей защита от точильщиков)
     * # Включить защиту от авто-точильщиков?
     * EnchantProtectEnable = true
     * # Период в течении которого, нужно набрать количество попыток заточить вещь.
     * EnchantProtectTime = 60
     * # Количество попыток заточить вещь.
     * EnchantProtectCount = 2
     * # Размер квадрата с картинками EnchantProtectImageCount*EnchantProtectImageCount
     * EnchantProtectImageCount = 9
     * # Наказание игрока, если он не дал верный ответ.
     * EnchantProtectPunishment = 0
     * # Тип протекта, 0 - по картинке, 1 - по числу
     * EnchantProtectType = 0
     **/
    public static boolean EnchantProtectEnable = false;
    public static long EnchantProtectTime = 3600;
    public static int EnchantProtectCount = 100;
    public static int EnchantProtectPunishment = 0;
    public static int EnchantProtectImageCount = 9;
    public static int EnchantProtectType = 0;
    public static int EnchantBotFail = -1;

    public static int MaxAttributeWeapon = 150;
    public static int MaxAttributeWeaponCrystal = 300;
    public static int MaxAttributeWeaponJewel = 450;
    public static int MaxAttributeWeaponEnergy = 600;

    public static int MaxAttributeArmor = 60;
    public static int MaxAttributeArmorCrystal = 120;
    public static int MaxAttributeArmorJewel = 180;
    public static int MaxAttributeArmorEnergy = 240;

    public static int EnchantAttributeJewelChance = 35;
    public static int EnchantAttributeEnergyChance = 30;
    public static int EChanceWeapon = 80;
    public static boolean AltEnchantFormulaForPvPServers = false;
    public static int SafeEnchant = 0;
    public static int SafeEnchantValueOlf = 0;
    public static int SafeEnchantPercent = 0;
    public static int SafeBlessedEnchantPercent = 0;
    public static int SafeEnchantPercentAccessory = 0;
    public static int SafeBlessedEnchantPercentAccessory = 0;
    public static int SafeEnchantPercentArmor = 0;
    public static int SafeBlessedEnchantPercentArmor = 0;
    public static int SafeEnchantCommon = 3;
    public static int SafeEnchantMasterYogi = 3;
    public static int SafeEnchantOlf = 3;
    public static int EnchantMaxArmor = 20;
    public static int EnchantAttributeChance = 50;
    public static int EnchantChanceCrystalArmor = 52;
    public static int EnchantMaxDestructionWeapon = 15;
    public static int EnchantMaxDestructionArmor = 6;
    public static int EnchantMaxJewelry = 20;
    public static int SafeEnchantFullBody = 4;
    public static boolean Enchant6AnnounceToAll = true;
    public static boolean EnchantUseAnimSkill = false;
    public static int ArmorOverEnchantHPBonusLimit = 9; // не знаю на кой но в старой системе конфигов было -3 от значения...
    public static int EnchantChanceBlessed = 68;
    public static int EnchantChanceArmorBlessed = 52;
    public static int EnchantChanceAccessoryBlessed = 54;
    public static boolean EnchantBonus = false;
    public static double EnchantBonusChance = 0.01;
    public static boolean EnchantBonusRandom = false;
    public static int[][] EnchantBonusList = {{57, 1000}};
    public static int[][] EnchantBonusRandomList = {{57, 1000}};

    public static int EnchantWeaponLevel = 1;
    public static int EnchantArmorLevel = 1;
    public static int EnchantAccessoryLevel = 1;

    public static int AttributeWeaponLevel = 5;
    public static int AttributeArmorLevel = 6;
    public static int AttributeStartWeapon = 20;

    public static boolean OfflikeEnchant = false;
    public static boolean OfflikeEnchantMage = false; // TODO: Реализовать.
    public static double OfflikeEnchantMageChance = 0.6667; // TODO: Реализовать.
    public static int[] OfflikeEnchantMasterYogi = {100, 100, 100, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 35, 35, 35, 35, 35};
    public static int[] OfflikeEnchantSimpleWeapon = {100, 100, 100, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 35, 35, 35, 35, 35};
    public static int[] OfflikeEnchantBlessedWeapon = {100, 100, 100, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 35, 35, 35, 35, 35};
    public static int[] OfflikeEnchantCrystalWeapon = {100, 100, 100, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 35, 35, 35, 35, 35};
    public static int[] OfflikeEnchantAncientWeapon = {100, 100, 100, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 35, 35, 35, 35, 35};
    public static int[] OfflikeEnchantSimpleArmor = {100, 100, 100, 66, 33, 25, 20, 16, 14, 12, 11, 10, 9, 8, 8, 7, 7, 6, 6, 6};
    public static int[] OfflikeEnchantBlessedArmor = {100, 100, 100, 66, 33, 25, 20, 16, 14, 12, 11, 10, 9, 8, 8, 7, 7, 6, 6, 6};
    public static int[] OfflikeEnchantCrystalArmor = {100, 100, 100, 66, 33, 25, 20, 16, 14, 12, 11, 10, 9, 8, 8, 7, 7, 6, 6, 6};
    public static int[] OfflikeEnchantAncientArmor = {100, 100, 100, 66, 33, 25, 20, 16, 14, 12, 11, 10, 9, 8, 8, 7, 7, 6, 6, 6};
    public static int[] OfflikeEnchantSimpleAccessory = {100, 100, 100, 66, 33, 25, 20, 16, 14, 12, 11, 10, 9, 8, 8, 7, 7, 6, 6, 6};
    public static int[] OfflikeEnchantBlessedAccessory = {100, 100, 100, 66, 33, 25, 20, 16, 14, 12, 11, 10, 9, 8, 8, 7, 7, 6, 6, 6};
    public static int[] OfflikeEnchantCrystalAccessory = {100, 100, 100, 66, 33, 25, 20, 16, 14, 12, 11, 10, 9, 8, 8, 7, 7, 6, 6, 6};
    public static int[] OfflikeEnchantAncientAccessory = {100, 100, 100, 66, 33, 25, 20, 16, 14, 12, 11, 10, 9, 8, 8, 7, 7, 6, 6, 6};
    public static int[] OfflikePremiumEnchantMasterYogi = {100, 100, 100, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 35, 35, 35, 35, 35};
    public static int[] OfflikePremiumEnchantSimpleWeapon = {100, 100, 100, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 35, 35, 35, 35, 35};
    public static int[] OfflikePremiumEnchantBlessedWeapon = {100, 100, 100, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 35, 35, 35, 35, 35};
    public static int[] OfflikePremiumEnchantCrystalWeapon = {100, 100, 100, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 35, 35, 35, 35, 35};
    public static int[] OfflikePremiumEnchantAncientWeapon = {100, 100, 100, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 35, 35, 35, 35, 35};
    public static int[] OfflikePremiumEnchantSimpleArmor = {100, 100, 100, 66, 33, 25, 20, 16, 14, 12, 11, 10, 9, 8, 8, 7, 7, 6, 6, 6};
    public static int[] OfflikePremiumEnchantBlessedArmor = {100, 100, 100, 66, 33, 25, 20, 16, 14, 12, 11, 10, 9, 8, 8, 7, 7, 6, 6, 6};
    public static int[] OfflikePremiumEnchantCrystalArmor = {100, 100, 100, 66, 33, 25, 20, 16, 14, 12, 11, 10, 9, 8, 8, 7, 7, 6, 6, 6};
    public static int[] OfflikePremiumEnchantAncientArmor = {100, 100, 100, 66, 33, 25, 20, 16, 14, 12, 11, 10, 9, 8, 8, 7, 7, 6, 6, 6};
    public static int[] OfflikePremiumEnchantSimpleAccessory = {100, 100, 100, 66, 33, 25, 20, 16, 14, 12, 11, 10, 9, 8, 8, 7, 7, 6, 6, 6};
    public static int[] OfflikePremiumEnchantBlessedAccessory = {100, 100, 100, 66, 33, 25, 20, 16, 14, 12, 11, 10, 9, 8, 8, 7, 7, 6, 6, 6};
    public static int[] OfflikePremiumEnchantCrystalAccessory = {100, 100, 100, 66, 33, 25, 20, 16, 14, 12, 11, 10, 9, 8, 8, 7, 7, 6, 6, 6};
    public static int[] OfflikePremiumEnchantAncientAccessory = {100, 100, 100, 66, 33, 25, 20, 16, 14, 12, 11, 10, 9, 8, 8, 7, 7, 6, 6, 6};
    public static int[] OfflikeSimpleEnchantOlf = {100, 100, 100, 70, 70, 70, 70, 70, 70, 70};
    public static int[] OfflikeBlessedEnchantOlf = {100, 100, 100, 70, 70, 70, 70, 70, 70, 70};
    public static int[] OfflikePremiumSimpleEnchantOlf = {100, 100, 100, 70, 70, 70, 70, 70, 70, 70};
    public static int[] OfflikePremiumBlessedEnchantOlf = {100, 100, 100, 70, 70, 70, 70, 70, 70, 70};

    /*************************** other.properties ***************************/
    public static int HellboundRescued = 1000;
    public static String GraphLineColor = "RED";
    public static int[] LethalNoImmune = {36606, 35062, 29021, 25451, 25452, 35410, 35409, 35408, 25709, 22217, 22216, 22215, 25622, 25621, 25620, 25619, 25618, 25617, 25616, 37000, 37001, 37002};
    public static int LethalImmuneHp = 0;
    public static String GraphAreaColor = "ORANGE";
    public static boolean AnnounceStartAuction = false;
    public static byte GetStartSubLevel = 40;
    public static boolean AnnounceShadaiSpawn = true;
    public static long UpdateDelay = 30;
    public static int AltItemAuctionExpiredAfter = 14;
    public static int AltItemAuctionTimeExtendsOnBid = 0;
    public static boolean GMHeroAura = false;
    public static int RateHbPoints = 1;
    public static boolean AltPetRecharge = true;
    public static boolean AltAttributePvPItem = false;
    public static boolean AltAugmentPvPItem = false;
    public static boolean AnnounceMammonSpawn = true;
    public static double RespawnRestoreHP = 65;
    public static int ChanceSpawnShadai = 40;
    public static String GMNameColour = "FFFFFF";
    public static int DeepBlueDropMaxDiff = 8;
    public static int DeepBlueSpoilMaxDiff = 8;
    public static boolean UseExtendedRRD = true;
    public static int GetStartLevel = 1;
    public static int MaximumQuestInventorySlot = 100;
    public static int PenaltyforEChanceToHandBlunt = 18;
    public static int MaxPvtStoreSlotsOther = 4;
    public static int MultisellPageSize = 40;
    public static String NormalNameColour = "FFFFFF";
    public static int EChanceMageWeapon = 80;
    public static boolean AlternativeCrystalScroll = false;
    public static double RespawnRestoreMP = -1;
    public static int GraphWidth = 580;
    public static int CastleSiegeDay = 14;
    public static boolean ShowHTMLWelcome = false;
    public static int toPvPItem = 0;
    public static int BaseWarehouseSlotsForDwarf = 120;
    public static int MaximumSlotsForDwarf = 100;
    public static float LineWidth = 1.0f;
    public static int EChanceArmor = 80;
    public static boolean InfinityArrow = false;
    public static boolean SendStatusTradeJustOffline = false;
    public static int TerritorySiegeDay = 14;
    public static int MaxPvtStoreSlotsDwarf = 5;
    public static int DISSOLVED_ALLY_PENALTY = 86400;
    public static String GraphPath = "./webserver/";
    public static int SwimingSpeedTemplate = 50;
    public static boolean InfinitySS = false;
    public static int MaximumSlotsForNoDwarf = 80;
    public static int GraphHeight = 378;
    public static int EChanceHigh = 1;
    public static int DeepBlueDropRaidMaxDiff = 2;
    public static int LEAVED_ALLY_PENALTY = 86400;
    public static int BaseWarehouseSlotsForNoDwarf = 100;
    public static int MaximumWarehouseSlotsForClan = 200;
    public static int toPvPItemCount = 0;
    public static boolean RegenSitWait = false;
    public static boolean setFameFoPVP = false;
    public static boolean setItemFoPVP = false;

    public static boolean setItemFoPVPCheckClan = false;
    public static boolean setItemFoPVPCheckHwid = false;
    public static boolean setItemFoPVPCheckIp = false;
    public static int setItemFoPVPMinLevel = 80;

    public static boolean UseDeepBlueDropRules = true;
    public static boolean AltItemAuctionEnabled = true;
    public static long MaxPlayerContribution = 1000000;
    public static int[] PvPZoneIds = {};
    public static boolean UseRRD = true;
    public static String ClanleaderNameColour = "FFFFFF";
    public static int MaxPvtManufactureSlots = 20;
    public static String RRDPath = "./config/";
    public static double RespawnRestoreCP = -1;
    public static boolean ApplyBuffOnPet = false;
    public static int EXPELLED_MEMBER_PENALTY = 86400;
    public static int EXPELLED_PLAYER_PENALTY = 86400;
    public static int DROP_CLAN_PENALTY = 864000;
    public static int EXPELLED_MEMBER_ALY_PENALTY = 86400;
    public static int MaximumSlotsForGMPlayer = 250;
    public static int PlayerUpLvlClan6 = 30;
    public static int PlayerUpLvlClan7 = 50;
    public static int PlayerUpLvlClan8 = 80;
    public static int PlayerUpLvlClan9 = 120;
    public static int PlayerUpLvlClan10 = 140;
    public static int PlayerUpLvlClan11 = 170;
    public static int ReputationUpLvlClan6 = 5000;
    public static int ReputationUpLvlClan7 = 10000;
    public static int ReputationUpLvlClan8 = 20000;
    public static int ReputationUpLvlClan9 = 40000;
    public static int ReputationUpLvlClan10 = 40000;
    public static int ReputationUpLvlClan11 = 75000;
    public static int GetHellboundLevel = -1;
    public static int SendMailLevel = 1;
    public static long ReSendMailTime = 10;
    public static long PriceSendMail = 0;
    /**
     * # Включить защиту от ботов?
     * BotProtectEnable = true
     * # Время на ответ?
     * # В секундах.
     * BotProtectRequestTime = 60
     * # Минимальное и Максимальное время проверки на бота.
     * # Выбирается каждый раз рандомное время из промежутка BotProtectTimeMin->BotProtectTimeMax
     * BotProtectTimeMin = 30
     * BotProtectTimeMax = 30
     **/
    public static boolean BotProtectEnable = false;
    public static long BotProtectRequestTime = 30;
    public static int BotProtectTimeMin = 30;
    public static int BotProtectTimeMax = 100;
    public static boolean BotProtectEnableZoneCheck = false;
    public static byte CreateClanLevel = 0;
    public static byte CreateClanRep = 0;
    public static int[] AuctionSetAucItem = {-1, -1, -1};

    /*************************** pvp.properties ***************************/
    public static float ChanceOfPKDropBase = 20f;
    public static boolean DropOnDie = false;
    public static int SPDivider = 7;
    public static float ChanceOfNormalDropBase = 1f;
    public static int MaxDropThrowDistance = 70;
    public static int MinPKToDropItems = 5;
    public static boolean CanGMDropEquipment = false;
    public static boolean CanTradeBanDropEquipment = false;
    public static boolean KarmaNeededToDrop = true;
    public static int ChanceOfDropOther = 80;
    public static int MinKarma = 720;
    public static int MaxItemsDroppable = 10;
    public static int BaseKarmaLost = 1200;
    public static float ChanceOfPKsDropMod = 1f;
    public static int[] ListOfNonDroppableItems = {57, 1147, 425, 1146, 461, 10, 2368, 7, 6, 2370, 2369, 3500, 3501, 3502, 4422, 4423, 4424, 2375, 6648, 6649, 6650, 6842, 6834, 6835, 6836, 6837, 6838, 6839, 6840, 5575, 7694, 6841, 8181};
    public static boolean DropAugmented = false;
    public static int PvPTime = 120000;
    public static int ChanceOfDropEquippment = 17;
    public static int ChanceOfDropWeapon = 3;
    public static int MaxKarmaIncrease = 10000;

    /*************************** residence.properties ***************************/
    public static int ClanHallBid_Grade2_MinClanMembers = 1;
    public static int ClanHallBid_Grade2_MinClanMembersAvgLevel = 1;
    public static int ClanHallBid_Grade3_MinClanMembersAvgLevel = 1;
    public static int ClanHallBid_Grade3_MinClanMembers = 1;
    public static int ClanHallBid_Grade1_MinClanLevel = 2;
    public static int ClanHallBid_Grade3_MinClanLevel = 2;
    public static double ResidenceLeaseFuncMultiplier = 1.;
    public static double ResidenceLeaseMultiplier = 1.;
    public static int ClanHallBid_Grade1_MinClanMembers = 1;
    public static int ClanHallBid_Grade2_MinClanLevel = 2;
    public static int ClanHallBid_Grade1_MinClanMembersAvgLevel = 1;
    public static int ClanHallBid_ItemId = 57;
    public static boolean ClanHallBidCharInventory = false;

    /*************************** items.properties ***************************/
    public static boolean PremiumUseEnable = false;
    public static boolean PremiumUseRandomDays = false;
    public static int[] PremiumUseItems = {4037};
    public static int[] PremiumUseDays = {4037};
    public static float[] PremiumUseValue = {2f};
    public static int[] PremiumRandomDays = {1, 30};

    /*************************** CharacterCreate.properties ***************************/
    public static boolean GiveStartPremium = false;
    public static int GiveStartPremiumCharCount = 1;
    public static float[] StartPremiumRate = {1, 30};
    public static String CnameTemplate = "([0-9A-Za-z]{2,16})|([0-9А-я]{2,16})";
    public static long StartingAdena = 0;
    public static int[] StartingItem = {};
    public static long[] StartingItemCount = {100};
    public static int[] StartingItemEnchant = {0};
    public static boolean CharTitle = false;
    public static String CharAddTitle = "Welcome";
    public static int NonAggroTime = 15;

    /*************************** server.properties ***************************/
    public static String[] NotCreateName = {};
    public static boolean ServerNormal = false;
    public static boolean ServerNoLabel = false;
    public static boolean ServerOnlyCreate = false;
    public static boolean ServerEvent = false;
    public static boolean ServerFree = false;
    public static boolean ServerClassic = false;
    public static boolean Autosave = true;
    public static boolean HardDbCleanUpOnStart = false;
    public static String AttributeBonusFile = "data/xml/attribute_bonus.xml";
    public static String LogChatDB = null;
    private static String ExternalHostname = "127.0.0.1";
    public static String InternalHostname = "127.0.0.1";
    public static boolean CCPGuardEnable = false;
    public static boolean StrixGuardEnable = false;
    public static boolean ScriptsGuardEnable = false;
    public static int CCPGuardSize = 80;
    public static boolean LameGuard = false;
    public static boolean ProtectEnable = false;
    public static boolean SmartGuard = false;
    public static boolean FirstTeam = false;
    public static boolean StartWhisoutSpawn = false;
    public static boolean ServerGMOnly = false;
    public static int MaxNPCAnimation = 90;
    public static int MinProtocolRevision = 267;
    public static String MAT_REPLACE_STRING = "[censored]";
    public static boolean MAT_ANNOUNCE_FOR_ALL_WORLD = true;
    public static int NoCarrierDefaultTime = 60;
    public static float RateClanRepScore = 1f;
    public static boolean saveAdminDeSpawn = true;
    public static String SnapshotsDirectory = develop ? "log/snapshots" : "./log/snapshots";
    public static float RateXp = 1f;
    public static String LogPacketsFromIPs = "";
    public static boolean HideGMStatus = true;
    public static int TradeChatMode = 1;
    public static boolean ServerListBrackets = false;
    public static String DefaultLang = "ru";
    public static int MovePacketDelay = 100;
    public static String Accounts_URL = develop ? "jdbc:mysql://localhost/l2tehno?useUnicode=true&characterEncoding=UTF-8" : "jdbc:mysql://localhost/l2open?useUnicode=true&characterEncoding=UTF-8";
    public static int minTRADElevel = 10;
    public static int SelectorSleepTime = 3;
    public static boolean RateQuestsRewardOccupationChange = true;
    public static int WeddingDivorceCosts = 20;
    public static int RaidMaxLevelDiff = 8;
    public static int IllegalActionPunishment = 1;
    public static int RateXpVitality = 1;
    public static long L2TopManagerInterval = 300000;
    public static int LoginPort = 9014;
    public static int LoginPorts[] = {9014};
    public static int DelayedItemsUpdateInterval = 10000;
    public static int IMCountToBest = 10;
    public static String L2TopWebAddress = "";
    public static boolean EnableNoCarrier = true;
    public static boolean AcceptAlternateID = true;
    public static int minTELLlevel = 10;
    public static int minALLlevel = 10;
    public static double RateDropItems = 1d;
    public static int MaxUnhandledSocketsPerIP = 5;
    public static boolean AllowWater = true;
    public static boolean WeddingPunishInfidelity = true;
    public static int[] GameserverPort = {7777};
    public static boolean AdvIPSystem = false;
    public static boolean TradeChatsReplaceExPattern = false;
    public static int RateFameReward = 1;
    public static int IdleConnectionTestPeriod = 60;
    public static String[] LogPacketsFromAccounts = null;
    public static int ScheduledThreadPoolSize = 16;
    public static boolean RatePartyMin = false;
    public static String ClanNameTemplate = "([0-9A-Za-z]{3,16})|([0-9А-я]{3,16})";
    public static boolean LoginUseCrypt = true;
    public static boolean MemorySnapshotOnShutdown = false;
    public static int UserInfoInterval = 100;
    public static double RateManor = 1;
    public static boolean LogChat = true;
    public static int AutoDestroyDroppedItemAfter = 600;
    public static String ApasswdTemplate = "([A-Za-z0-9]{4,24})";
    public static boolean TestServer = false;
    public static boolean DropCursedWeaponsOnKick = false;
    public static String LoginHost = "127.0.0.1";
    public static String LoginHosts[] = {"127.0.0.1"};
    public static long TimeOutChecker = 60000;
    public static String ClanTitleTemplate = "[A-Za-z0-9А-Яа-я -\\[\\]<>\\(\\)!|]{1,16}";
    public static boolean UseDatabaseLayer = true;
    public static int L2WalkerPunishment = 0;
    public static boolean MultiThreadedIdFactoryCleaner = false;
    public static int ChatLineLength = -1;
    public static boolean ServerListClock = false;
    public static boolean ShowGMLogin = false;
    public static int MinNPCAnimation = 30;
    public static int PurgeTaskFrequency = 60;
    public static int AutoRestartAt = 5;
    public static String LicenseKey = "tests11";
    public static int WeddingTeleportInterval = 120;
    public static boolean ForceStatusUpdate = false;
    public static int GlobalChat = 0;
    public static String DatapackRoot = ".";
    public static boolean ServerSideNpcName = false;
    public static int LogPacketsFlushSize = 8192;
    public static float RateQuestsDropProf = 1f;
    public static String Login = "root";
    public static int GlobalTradeChat = 0;
    public static String[] LogPacketsFromChars = null;
    public static int RequestServerID = 1;
    public static boolean useFileCache = true;
    public static int ShoutChatMode = 1;
    public static int AttackPacketDelay = 200;
    public static float RateQuestsDrop = 1f;
    public static boolean TradeChatsReplaceFromAll = false;
    public static String AllyNameTemplate = "([0-9A-Za-z]{3,16})|([0-9А-я]{3,16})";
    public static boolean MAT_ANNOUNCE_NICK = true;
    public static int minSHOUTlevel = 10;
    public static boolean L2TopManagerEnabled = false;
    public static boolean WeddingAllowSameSex = true;
    public static int Timer_to_UnBan = 5;
    public static String L2TopSmsAddress = "";
    public static float RateSp = 1f;
    public static float RateDropAdenaStaticMod = 0f;
    public static boolean EngQuestNames = false;
    public static boolean AllowCursedWeapons = true;
    public static boolean AntiFloodEnable = false;
    public static int BugUserPunishment = 2;
    public static boolean LogKills = true;
    public static boolean isAutoUpdate = false;
    public static boolean ServerSideNpcTitle = false;
    public static boolean TradeChatsReplaceFromShout = false;
    public static boolean LogServerPackets = false;
    public static boolean LazyItemUpdateAll = false;
    public static boolean MAT_REPLACE = false;
    public static String TradeWords = "продаю,проgаю,пр0даю,продам,проgам,пр0дам,покупаю,куплю,кyплю,обменяю,выменяю,ВТТ,ВТС,ВТБ,WTB,WTT,WTS";
    public static boolean DamageFromFalling = true;
    public static String GuardType = "NONE";
    public static String GeoEditorHost = "127.0.0.1";
    public static boolean GGCheck = false;
    public static boolean MAT_ANNOUNCE = true;
    public static float RateRaidRegen = 1f;
    public static float RateDropCommonItems = 1f;
    public static String Accounts_Login = "root";
    public static String Driver = "com.mysql.jdbc.Driver";
    public static int MaximumOnlineUsers = 3000;
    public static float RateDropAdenaMultMod = 1f;
    public static int BroadcastCharInfoInterval = 100;
    public static int UnhandledSocketsMinTTL = 5000;
    public static long[] L2TopReward = {57, 20, 4037, 1};
    public static boolean EverybodyHasAdminRights = false;
    public static boolean AllowWarehouse = true;
    public static int DeadLockCheck = 10000;
    public static int LazyItemUpdateAllTime = 60000;
    public static boolean LogClientPackets = false;
    public static int RateSpVitality = 1;
    public static boolean checkLangFilesModify = false;
    public static int RateClanRepScoreMaxAffected = 2;
    public static boolean ServerSideNpcTitleWithLvl = false;
    public static boolean AllowWedding = false;
    public static boolean BroadcastStatsInterval = true;
    public static int WeddingTeleportPrice = 500;
    public static int MaxIdleConnectionTimeout = 600;
    public static boolean AllowFreight = false;
    public static int NoCarrierMinTime = 0;
    public static String WebServerRoot = "./webserver/";
    public static boolean MultiThreadedIdFactoryExtractor = true;
    public static int MaxProtocolRevision = 273;
    public static String L2TopServerAddress = "open-team.ru";
    public static int WebServerDelay = 10;
    public static int LazyItemUpdateTime = 60000;
    public static int LinearTerritoryCellSize = 32;
    public static boolean ParalizeOnRaidLevelDiff = true;
    public static float RateDropSpoil = 1f;
    public static float RateCountDropSpoil = 1f;
    public static int SaveGameTimeInterval = 120;
    public static int RateMaxIterations = 30;
    public static boolean InterestAlt = true;
    public static int AutoRestart = 0;
    public static int ChatMaxLines = 5;
    public static float RateDropChest = 1f;
    public static boolean AllowDiscardItem = true;
    public static float RateFishDropCount = 1f;
    public static int NoCarrierMaxTime = 90;
    public static int[] MAT_BAN_CHANNEL = {};
    public static int ChatMessageLimit = 1000;
    public static String Accounts_Password = "";
    public static int ExecutorThreadPoolSize = 8;
    public static int AutoDestroyPlayerDroppedItemAfter = 7200;
    public static float RateQuestsRewardAdena = 1f;
    public static float RateQuestsRewardDrop = 1f;
    public static float RateQuestsRewardExpSp = 1f;
    public static String Password = develop ? System.getenv("PASSWORD") : "root";
    public static boolean AllowBoat = true;
    public static int MaximumDbConnections = 50;
    public static int RateDropEpaulette = 1;
    public static int[] DisableCreateItems = {};
    public static String URL = develop ? "jdbc:mysql://localhost/l2tehno?useUnicode=true&characterEncoding=UTF-8" :"jdbc:mysql://localhost/l2open?useUnicode=true&characterEncoding=UTF-8";
    public static int InterestMaxThread = 15;
    public static int RateBreakpoint = 15;
    public static int WeddingPrice = 500000;
    public static int WaitPingTime = 5;
    public static boolean StartWhisoutQuest = false;
    public static boolean SaveGMEffects = true;
    public static int DeleteCharAfterDays = 7;
    public static int DeleteCharAfterMin = -1;
    public static boolean LazyItemUpdate = true;
    public static boolean LogTelnet = true;
    public static boolean SqlLog = false;
    public static boolean MainLog = false;
    public static double RateRaidBoss = 1d;
    public static double RateEpicBoss = 1d;
    public static double RateDropAdena = 1d;
    public static int L2TopSaveDays = 30;
    public static String GameserverHostname = "*";
    public static boolean PingServer = true;
    public static boolean UseClientLang = false;
    public static boolean WeddingFormalWear = true;
    public static boolean AllowSpecialCommands = false;
    public static boolean WeddingTeleport = true;
    public static boolean saveAdminSpawn = true;
    public static boolean MAT_BANCHAT = false;
    public static boolean LogMultisellToSql = false;
    public static boolean StatusVoiceCommandEnabled = false;
    public static int[] LogMultisellId = {};
    public static int cNameMaxLen = 32;
    public static byte[] HexID;
    public static int ShoutChatRadius = 10000;
    public static int TradeChatRadius = 10000;
    public static boolean SAEnabled = false;
    public static boolean SABanAccEnabled = false;
    public static boolean SAStrongPass = false;
    public static int SAMaxAttemps = 5;
    public static int SABanTime = 480;
    public static String SARecoveryLink = "http://www.my-domain.com/charPassRec.php";
    public static boolean DetailLogItem2 = false; // детальный лог создания ВСЕХ, ДАЖЕ ВРЕМЕННЫХ итемов, будет выводить информацию о том какой участок кода использовался для создания итема
    public static int ShoutOffset = 0;
    public static boolean setAutoCommitOnClose = true;
    public static int setInitialPoolSize = 1;
    public static int setMinPoolSize = 1;
    public static int setAcquireRetryAttempts = 0;
    public static int setAcquireRetryDelay = 100;
    public static int setCheckoutTimeout = 0;
    public static int setAcquireIncrement = 5;
    public static int setMaxStatements = 100;
    public static int setMaxStatementsPerConnection = 10;
    public static int setNumHelperThreads = 5;
    public static boolean setBreakAfterAcquireFailure = false;
    public static int GSLSConnectionSleep = 10;
    public static int ByPassFailCount = 3;
    public static int ByPassFailPunishment = 1;
    public static int ByPassFailPunishmentTime = 10;
    public static int SendStatusAddPerOnline = 0;
    public static int StorageNpcCap = 60000;
    public static int StorageItemCap = 240000;
    public static int StorageOtherCap = 5000;
    public static int StorageNpcInit = 5000;
    public static int StorageItemInit = 3000;
    public static int StorageOtherInit = 1000;
    public static boolean GlobalChatForPremium = false;
    public static int CommunityFailPunishment = 1;
    public static int CommunityFailPunishmentTime = 10;
    public static String L2TopNamePrefix = "";
    public static boolean MmoTopStartWithServer = false;
    public static boolean MmoTopShowConsoleInfo = true;
    public static long MmoTopManagerInterval = 300000;
    public static String MmoTopWebAddress = "";
    public static long[] MmoTopReward = {57, 20, 4037, 1};
    public static long[] MmoTopRewardSms = {};
    public static long[] MmoTopRewardRnd = {};
    public static long[] MmoTopRewardRndSms = {};
    public static boolean ChatLimitZoneCheck = false;

    public static boolean MmoVoteStartWithServer = false;
    public static String MmoVoteWebAddress = "";
    public static long[] MmoVoteReward = {57, 20, 4037, 1};
    public static long MmoVoteManagerInterval = 300000;
    public static long QuestSagasRewardAdenaCount = 5000000;
    public static long QuestSagasRewardCodexCount = 1;

    /*************************** services.properties ***************************/
    public static boolean TradeZoneGiranHarbor = false;
    public static boolean AccHwidLockEnable = false;
    public static int AccHwidLockClear = 0;
    public static int AccHwidLockPriceId = 4037;
    public static long AccHwidLockPriceCount = 100;
    public static int BONUS_DAYS = 7;
    public static boolean GiranHarborZone = false;
    public static double Lottery3NumberRate = 0.2;
    public static int BufferPrice = 5000;
    public static int ExpandInventoryMax = 250;
    public static boolean ParnassusZone = false;
    public static int WindowDays = 7;
    public static boolean NoCastleTaxInOffshore = false;
    public static boolean BashSkipDownload = false;
    public static boolean AllowReferrals = false;
    public static int SeparateSubItem = 4037;
    public static boolean CM_CoLShop = false;
    public static int WindowMax = 3;
    public static int BabyPetExchangeItem = 4037;
    public static int OfflineTradeDaysToKick = 14;
    public static boolean RateBonusEnabled = false;
    public static int BufferMaxLvl = 99;
    public static boolean NoblessTWEnabled = true;
    public static int BashReloadTime = 1;
    public static boolean NickColorChangeEnabled = false;
    public static boolean NickChangeEnabled = false;
    public static boolean ExpandInventoryEnabled = false;
    public static int BONUS_RATE = 2;
    public static int OfflineMinLevel = 40;
    public static int OfflineTradePrice = 0;
    public static int ClanNameChangePrice = 1000;
    public static int PetNameChangeItem = 4037;
    public static boolean BabyPetExchangeEnabled = false;
    public static boolean CM_BasicShop = false;
    public static int[] RateBonusItem = {4037, 4037};
    public static int ExpandCWHItem = 4037;
    public static boolean NoTradeOnlyOffline = false;
    public static boolean TradeOnlyPice = false;
    public static boolean RateBonusApplyRatesThenServiceDisabled = false;
    public static int AltLotteryPrice = 2000;
    public static double Lottery4NumberRate = 0.4;
    public static int SexChangePrice = 100;
    public static int BufferMinLvl = 1;
    public static int NickChangePrice = 100;
    public static int ExpandCWHPrice = 1000;
    public static int ExpandCWHCount = 1;
    public static int SeparateSubPrice = 35;
    public static boolean ExpandCWHEnabled = false;
    public static boolean AllowLottery = false;
    public static int LotteryTicketPrice = 2000;
    public static boolean ChangePassword = false;
    public static int ParnassusPrice = 500000;
    public static int[] RateBonusPrice = {1500, 250};
    public static String OfflineTradeNameColor = "A0FFFF";
    public static int[] AllowClassMasters = {0, 0, 0};
    public static double OffshoreTradeTax = 0.0;
    public static boolean OfflineRestoreAfterRestart = true;
    public static boolean BONUS_ENABLED = false;
    public static boolean Short2ndProfQuest = true;
    public static boolean PetNameChangeEnabled = false;
    public static long LotteryPrize = 50000;
    public static boolean BashEnabled = false;
    public static int BONUS_PRICE = 50;
    public static boolean BufferPetEnabled = false;
    public static int NickColorChangeItem = 4037;
    public static int ExpandInventoryPrice = 1000;
    public static int PetNameChangePrice = 100;
    public static int NoblessSellItem = 4037;
    public static int BabyPetExchangePrice = 100;
    public static boolean SeparateSubEnabled = false;
    public static boolean NoblessSellEnabled = false;
    public static boolean TradeTaxOnlyOffline = false;
    public static int ReferralsBonusCount2 = 100;
    public static int ReferralsBonusId2 = 57;
    public static int ReferralsBonusCount1 = 100;
    public static int ReferralsBonusId1 = 57;
    public static int ReferralsBonusCount3 = 100;
    public static int ReferralsBonusId3 = 57;
    public static int RouletteMaxBet = 1000000000;
    public static String SellPets = "";
    public static boolean BaseChangeEnabled = false;
    public static int NickChangeItem = 4037;
    public static String NickChangeSymbolTemp = "([+=#!@%^$&?~.0-9A-Za-z\u003c\u003e\u0028\u0029\u007b\u007d\u005f\u007c\u002d\u00ae\u00a9]{2,16})|([+=#!@%^$&?~.0-9\u0410-\u044f\u003c\u003e\u0028\u0029\u007b\u007d\u005f\u007c\u002d\u00ae\u00a9]{2,16})";
    public static int[] RateBonusTime = {30, 2};
    public static String[] NickColorChangeList = {"00FF00", "191971", "00BFFF", "53868B", "00E5EE", "7FFFD4", "54FF9F", "006400", "FFFF00", "EE9A00", "8B5A00", "0080FF", "708090", "05C1FF", "9C9CF2", "05FFB5"};
    public static boolean ParnassusNoTax = false;
    public static boolean TradeOnlyFar = false;
    public static boolean SexChangeEnabled = false;
    public static boolean WindowEnabled = false;
    public static int CharToAccItem = 4037;
    public static int ExpandWarehouseItem = 4037;
    public static double TradeTax = 0.0;
    public static int NickColorChangePrice = 100;
    public static int ExpandWarehousePrice = 1000;
    public static boolean KickOfflineNotTrading = true;
    public static int ClanNameChangeItem = 4037;
    public static boolean ClanNameChangeEnabled = false;
    public static int TradeRadius = 30;
    public static int BONUS_ITEM = 4037;
    public static int Lottery2and1NumberPrize = 200;
    public static boolean AllowOfflineTrade = false;
    public static boolean HowToGetCoL = false;
    public static boolean CharToAcc = false;
    public static boolean ExpandWarehouseEnabled = false;
    public static double Lottery5NumberRate = 0.6;
    public static boolean AllowRoulette = false;
    public static int WindowPrice = 1000;
    public static int[] ClassMastersPriceItem = {57, 57, 57};
    public static int OfflineTradePriceItem = 57;
    public static boolean BufferEnabled = false;
    public static boolean LockAccountIP = false;
    public static int CharToAccPrice = 500;
    public static int RouletteMinBet = 1000;
    public static int WindowItem = 4037;
    public static int BaseChangePrice = 35;
    public static int ExpandInventoryItem = 4037;
    public static int BaseChangeItem = 4037;
    public static float[] RateBonusValue = {2f, 2f};
    public static int[] ClassMastersPrice = {300000, 3000000, 50000000};
    public static int SexChangeItem = 4037;
    public static int NoblessSellPrice = 1000;
    public static boolean PLRM_Enable = false;
    public static int PLRM_RewardsSubCount = 3;
    public static boolean PLRM_RewardsTypeNew = false;

    public static String PLRM_Rewards = "30, 4037, 5;61, 4037, 10;30, 4037, 50;"; /* Формат: уровень,ид_айтема,количество;уровень2,ид_айтема2,количество2 */
    public static boolean PLRM_NewFormatRewards = false;
    public static boolean PLRM_PrintInfo = false; /* Выводить в консоль инфо о вручении наград */
    public static boolean PLRM_SendMessageToChar = true; /* Посылать сообщение чару о получении награды */
    public static String PLRM_MessageToChar = "You received level reward for level: %level%";
    public static float RATE_TOKEN = 1f;
    public static float[] BONUS_RATE_ALL = new float[0];
    public static float[] BONUS_RATE_XP = new float[0];
    public static float[] BONUS_RATE_SP = new float[0];
    public static float[] BONUS_RATE_QUESTS_REWARD = new float[0];
    public static float[] BONUS_RATE_QUESTS_DROP = new float[0];
    public static float[] BONUS_RATE_DROP_ADENA = new float[0];
    public static float[] BONUS_RATE_DROP_ITEMS = new float[0];
    public static float[] BONUS_RATE_DROP_SPOIL = new float[0];
    public static float[] BONUS_RATE_FAME = new float[0];
    public static float[] BONUS_RATE_EPAULETTE = new float[0];

    public static float[] BONUS_RATE_TOKEN = new float[0];
    public static float[] BONUS_RATE_MAX_LOAD = new float[0];
    public static float[] BONUS_RATE_CRAFT = new float[0];
    public static float[] BONUS_RATE_CRAFT_MASTER_WORK = new float[0];
    public static float[] BONUS_RATE_ENCHANT = new float[0];
    public static float[] BONUS_RATE_ENCHANT_BLESSED = new float[0];
    public static float[] BONUS_RATE_ENCHANT_MUL = new float[0];
    public static float[] BONUS_RATE_ENCHANT_BLESSED_MUL = new float[0];

    public static int[] BONUS_PRICE_INDEX = new int[0];
    public static int[] BONUS_PRICE_ALL = new int[0];
    public static int[] BONUS_PRICE_XP = new int[0];
    public static int[] BONUS_PRICE_SP = new int[0];
    public static int[] BONUS_PRICE_QUESTS_REWARD = new int[0];
    public static int[] BONUS_PRICE_QUESTS_DROP = new int[0];
    public static int[] BONUS_PRICE_DROP_ADENA = new int[0];
    public static int[] BONUS_PRICE_DROP_ITEMS = new int[0];
    public static int[] BONUS_PRICE_DROP_SPOIL = new int[0];
    public static int[] BONUS_PRICE_RATE_FAME = new int[0];
    public static int[] BONUS_PRICE_RATE_EPAULETTE = new int[0];
    public static int[] BONUS_DAY = new int[0];
    public static int[] BONUS_PRICE_ID = {4037};
    public static boolean SellHeroItemForPremium = false;
    public static boolean DellClientIfOffline = false;
    public static boolean OlympiadPointsSellEnabled = false;
    public static int OlympiadPointsSellLimit = 0;
    public static int TradeMinLevel = 0;
    public static int TitleColorChangePrice = 100;
    public static int TitleColorChangeItem = 4037;
    public static String[] TitleColorChangeList = {"00FF00", "191971", "00BFFF", "53868B", "00E5EE", "7FFFD4", "54FF9F", "006400", "FFFF00", "EE9A00", "8B5A00", "0080FF", "708090", "05C1FF", "9C9CF2", "05FFB5"};
    public static boolean TitleColorChangeFreePremium = false;
    public static boolean NickColorChangeFreePremium = false;
    public static int ItemBrokerUpdateTime = 300;
    public static int[] OfftradeItem = {-1};

    public static boolean ClanLevelEnable = false;
    public static int[] ClanLevelItem = {57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57};
    public static int[] ClanLevelPrice = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};

    public static boolean ClanPointEnable = false;
    public static int ClanPointItem = 4037;
    public static long[] ClanPointPrice = {1000, 1};

    public static boolean LevelManipulationEnable = false;
    public static int[] LevelUp = {57, 10000};
    public static int[] DeLevel = {57, 5000};

    public static float AddHwidBonus = 1f;
    public static boolean AccHwidLockOnlyFullInfo = false;

    public static boolean ActivationEnable = false;
    public static boolean ActivationCanNewMail = false;
    public static boolean ActivationCanNewQuestionAndAnswer = true;
    public static boolean ActivationCanNewPass = true;

    /*************************** siege_castle.properties ***************************/
    public static int ControlTowerLosePenalty = 150000;
    public static String GoddardArtefact2 = "148353,-50450,-1505,0,35323";
    public static String InnadrilControlTower4 = "116261,245621,-699,13002,6000";
    public static String GoddardArtefact1 = "146601,-50450,-1505,32768,35322";
    public static String InnadrilControlTower3 = "115977,251223,-699,13002,6000";
    public static String AdenControlTower8 = "149976,1585,-494,13002,6000";
    public static String InnadrilControlTower2 = "116037,249948,-669,13002,6000";
    public static String GludioArtefact1 = "-18120,107984,-2483,16384,35063";
    public static String AdenControlTower7 = "144954,1603,-494,13002,6000";
    public static String InnadrilControlTower1 = "116062,248649,-973,13002,6000";
    public static String GiranArtefact1 = "117939,145090,-2550,32768,35147";
    public static String AdenControlTower6 = "146137,2351,-426,13002,6000";
    public static String AdenControlTower5 = "148775,2351,-426,13002,6000";
    public static String AdenControlTower4 = "148755,6930,-426,13002,6000";
    public static String AdenControlTower3 = "146158,6929,-426,13002,6000";
    public static String AdenControlTower2 = "147460,1303,-176,13002,6000";
    public static String AdenControlTower1 = "147455,5624,-911,13002,6000";
    public static String InnadrilArtefact1 = "116031,250555,-798,49200,35279";
    public static String DionControlTower4 = "22319,156863,-2603,13002,8000";
    public static String DionControlTower3 = "22027,162449,-2603,13002,80000";
    public static String DionControlTower2 = "22138,159901,-2877,13002,20000";
    public static String DionControlTower1 = "22158,161167,-2573,13002,8000";
    public static String GoddardControlTower4 = "147477,-48516,-505,13002,6000";
    public static String GoddardControlTower3 = "144741,-48188,-1744,13002,6000";
    public static String GoddardControlTower2 = "150183,-48201,-1744,13002,6000";
    public static String AdenArtefact1 = "147465,1537,-373,16384,35233";
    public static String GoddardControlTower1 = "147456,-46029,-1360,13002,6000";
    public static String SchuttgartControlTower4 = "77568,-152541,1226,13002,6000";
    public static String GludioControlTower4 = "-18359,112879,-2409,13002,8000";
    public static String SchuttgartControlTower3 = "74862,-152162,-12,13002,6000";
    public static String GludioControlTower3 = "-18061,107294,-2409,13002,8000";
    public static String SchuttgartControlTower2 = "80306,-152257,-12,13002,6000";
    public static String GludioControlTower2 = "-18137,108583,-2379,13002,20000";
    public static String GludioControlTower1 = "-18134,109785,-2683,13002,8000";
    public static String RuneArtefact1 = "9132,-49153,1094,64270,35469";
    public static String SchuttgartControlTower1 = "77561,-150087,371,13002,6000";
    public static String OrenControlTower4 = "79103,36942,-2203,13002,10000";
    public static String OrenControlTower3 = "84709,37234,-2203,13002,10000";
    public static String OrenControlTower2 = "82129,37131,-2477,13002,30000";
    public static String DionArtefact1 = "22081,161771,-2677,49017,35105";
    public static String OrenControlTower1 = "83416,37164,-2173,13002,10000";
    public static String SchuttgartArtefact2 = "78446,-154524,225,0,35514";
    public static String SchuttgartArtefact1 = "76668,-154520,225,0,35515";
    public static String GiranControlTower4 = "113049,144849,-2476,13002,10000";
    public static String GiranControlTower3 = "116116,145016,-2750,13002,10000";
    public static String GiranControlTower2 = "117339,145051,-2446,13002,30000";
    public static String RuneControlTower6 = "12259,-47510,1295,13002,6000";
    public static String GiranControlTower1 = "118623,145150,-2476,13002,10000";
    public static String RuneControlTower5 = "14822,-51282,1027,13002,6000";
    public static String RuneControlTower4 = "14796,-47041,1027,13002,6000";
    public static String OrenArtefact1 = "84014,37184,-2277,32768,35189";
    public static String RuneControlTower3 = "16727,-47952,-641,13002,6000";
    public static String RuneControlTower2 = "16690,-50330,-641,13002,6000";
    public static String RuneControlTower1 = "18260,-49161,-571,13002,6000";
    public static int[] CastleSelectHours = {16, 20};
    public static int CastleSelectHoursTime = 86400000;

    /*************************** siege_clanhall.properties ***************************/
    public static int BeastFarmSiegeHour = 19;
    public static String N64SiegeBoss3 = "58285,-27269,576,0,35631";
    public static String N64SiegeBoss2 = "58304,-27736,576,0,35630";
    public static String N64SiegeBoss1 = "58583,-27508,576,0,35629";
    public static String N34SiegeBoss3 = "178298,-17624,-2194,0,35410";
    public static String N34SiegeBoss2 = "178304,-17712,-2194,0,35409";
    public static String N34SiegeBoss1 = "178306,-17535,-2195,0,35408";
    public static String N21SiegeBoss1 = "44525,108867,-2020,0,35368";
    public static int RainbowSprSiegeHour = 19;
    public static String N64Messenger1 = "58080,-31930,301,43134,35639";
    public static int BanditStrSiegeHour = 19;
    public static String N21Messenger1 = "50343,111282,-1970,0,35382";
    public static int RainbowSprSiegeDay = 6;
    public static int BeastFarmSiegeDay = 6;
    public static int BanditStrSiegeDay = 6;
    public static String N34Messenger1 = "179040,-13717,-2263,5456,35420";

    /*************************** siege_fortress.properties ***************************/
    public static String N106Flag3 = "154742,55541,-3123,9819";
    public static String N106Flag2 = "154687,55480,-3123,9819";
    public static String N106Flag1 = "154638,55407,-3123,9819";
    public static String N111GuardDoor2 = "23130008;1";
    public static String N111GuardDoor1 = "23130007;1";
    public static int N118CommandCenterDoor4 = 23200009;
    public static int N118CommandCenterDoor3 = 23200008;
    public static int N118CommandCenterDoor2 = 23200002;
    public static int N118CommandCenterDoor1 = 23200003;
    public static int N105CommandCenterDoor4 = 22180005;
    public static String N118GuardDoor4 = "23200011;1";
    public static int N105CommandCenterDoor3 = 22180004;
    public static String N118GuardDoor3 = "23200010;1";
    public static int N105CommandCenterDoor2 = 22180002;
    public static String N118GuardDoor2 = "23200007;2";
    public static int N105CommandCenterDoor1 = 22180003;
    public static String N110GuardDoor4 = "22160007;1";
    public static String N118GuardDoor1 = "23200006;2";
    public static String N114GuardDoor2 = "21220005;1";
    public static String N110GuardDoor3 = "22160006;1";
    public static String N114GuardDoor1 = "21220004;1";
    public static String N110GuardDoor2 = "22160010;2";
    public static String N110GuardDoor1 = "22160011;2";
    public static String N115Flag3 = "11623,95311,-3264,9819";
    public static String N115Flag2 = "11527,95301,-3264,9819";
    public static String N115Flag1 = "11459,95308,-3264,9819";
    public static String N112Commander4 = "5231,152383,-2848,49686,36038,36066";
    public static String N112Commander3 = "4371,150863,-2862,62758,36039,36061";
    public static String N112Commander2 = "6976,148253,-2862,32633,36036,36058";
    public static String N112Commander1 = "6521,151872,-2610,37604,36037,36064";
    public static String N116FlagPole1 = "79274,91070,-1987,0,22200500";
    public static String N119Commander3 = "71534,186398,-2579,54476,36290,36308";
    public static String N119Commander2 = "71411,184752,-2580,6012,36288,36305";
    public static String N115Commander3 = "12829,96214,-3392,49152,36145,36163";
    public static String N119Commander1 = "74279,186914,-2327,4837,36289,36311";
    public static String N115Commander2 = "9472,94992,-3392,0,36143,36160";
    public static String N115Commander1 = "13184,94928,-3144,0,36144,36166";
    public static String N121Envoy2 = "9;36455;72240;-94392;-1264;12000";
    public static String N121Envoy1 = "8;36433;72370;-94472;-1264;12000";
    public static String N109FlagPole1 = "159111,-70289,-1967,0,24150500";
    public static String N120Commander3 = "99479,-54154,-618,62454,36322,36344";
    public static String N120Commander2 = "100676,-57353,-618,16184,36319,36341";
    public static String N120Commander1 = "100767,-53668,-366,60699,36320,36347";
    public static int N110CommandCenterDoor4 = 22160004;
    public static int N110CommandCenterDoor3 = 22160005;
    public static int N110CommandCenterDoor2 = 22160009;
    public static int N110CommandCenterDoor1 = 22160008;
    public static String N102FlagPole1 = "-22671,219813,-2329,0,19240500";
    public static int N109CommandCenterDoor4 = 24150005;
    public static int N109CommandCenterDoor3 = 24150004;
    public static int N109CommandCenterDoor2 = 24150006;
    public static String N110Envoy1 = "8;36452;69992;-61096;-2624;12642";
    public static int N109CommandCenterDoor1 = 24150007;
    public static String N105Flag3 = "72748,4534,-2914,9819";
    public static String N105Flag2 = "72677,4482,-2914,9819";
    public static String N105Flag1 = "72625,4423,-2914,9819";
    public static String N114Flag3 = "60418,139681,-1623,9819";
    public static String N114Flag2 = "60341,139701,-1623,9819";
    public static String N114Flag1 = "60256,139716,-1623,9819";
    public static String N117Envoy3 = "7;36453;111464;-14792;-832;16384";
    public static String N117Envoy2 = "5;36445;111368;-14792;-832;16384";
    public static String N117Envoy1 = "4;36443;111256;-14792;-832;16384";
    public static String N118FlagPole1 = "125245,95175,-1243,0,23200500";
    public static String N121Flag3 = "72338,-94542,-1297,9819";
    public static String N121Flag2 = "72260,-94509,-1297,9819";
    public static String N121FlagPole1 = "72183,-94726,-531,0,22150500";
    public static String N121Flag1 = "72162,-94476,-1297,9819";
    public static String N113Envoy1 = "1;36437;-53240;91608;-2664;16384";
    public static int N114CommandCenterDoor4 = 21220002;
    public static int N114CommandCenterDoor3 = 21220003;
    public static int N114CommandCenterDoor2 = 21220007;
    public static int N114CommandCenterDoor1 = 21220006;
    public static int N101CommandCenterDoor4 = 18220002;
    public static int N101CommandCenterDoor3 = 18220003;
    public static int N101CommandCenterDoor2 = 18220005;
    public static int N101CommandCenterDoor1 = 18220004;
    public static String N104FlagPole1 = "126081,123393,-1688,0,23210500";
    public static String N102Envoy1 = "1;36394;-22386;219807;-3079;0";
    public static int N115CommandCenterDoor4 = 20200007;
    public static int N115CommandCenterDoor3 = 20200006;
    public static int N115CommandCenterDoor2 = 20200003;
    public static String N102GuardDoor4 = "19240012;1";
    public static int N115CommandCenterDoor1 = 20200002;
    public static String N102GuardDoor3 = "19240011;1";
    public static String N102GuardDoor2 = "19240002;2";
    public static String N102GuardDoor1 = "19240001;2";
    public static String N104Flag3 = "126157,123622,-2454,9819";
    public static String N104Flag2 = "126072,123622,-2454,9819";
    public static String N104Flag1 = "125991,123616,-2454,9819";
    public static String N109GuardDoor4 = "24150012;1";
    public static String N109GuardDoor3 = "24150011;1";
    public static String N109GuardDoor2 = "24150002;2";
    public static String N109GuardDoor1 = "24150001;2";
    public static String N105GuardDoor2 = "22180007;1";
    public static String N105GuardDoor1 = "22180006;1";
    public static String N116Envoy2 = "4;36438;79530;91288;-2720;0";
    public static String N116Envoy1 = "3;36440;79530;91144;-2720;0";
    public static String N107Commander4 = "191896,39784,-3368,31794,35862,35890";
    public static String N107Commander3 = "188532,41045,-3381,54396,35863,35885";
    public static String N107Commander2 = "188162,39846,-3383,50223,35860,35882";
    public static String N103Commander3 = "15999,189518,-2888,53417,35731,35749";
    public static String N107Commander1 = "188637,38246,-3131,8193,35861,35888";
    public static String N103Commander2 = "18005,187583,-2896,41985,35729,35746";
    public static String N103Commander1 = "15127,188103,-2640,47750,35730,35752";
    public static String N113Flag3 = "-53151,91494,-2690,9819";
    public static String N113Flag2 = "-53240,91497,-2690,9819";
    public static String N109Envoy1 = "7;36401;158856;-70120;-2704;24576";
    public static String N113Flag1 = "-53324,91490,-2690,9819";
    public static String N106Commander3 = "153370,54748,-3223,63572,35831,35849";
    public static String N106Commander2 = "155591,56631,-3227,58176,35829,35846";
    public static String N105Envoy1 = "4;36397;72632;4552;-2888;21000";
    public static String N106Commander1 = "154711,53864,-2973,31823,35830,35852";
    public static String N120Flag3 = "100455,-55253,-488,9819";
    public static String N120Flag2 = "100455,-55343,-488,9819";
    public static String N120Flag1 = "100462,-55413,-488,9819";
    public static String N117GuardDoor4 = "23170011;1";
    public static String N117GuardDoor3 = "23170010;1";
    public static String N113GuardDoor4 = "18200009;1";
    public static String N117GuardDoor2 = "23170002;2";
    public static String N113GuardDoor3 = "18200008;1";
    public static String N117GuardDoor1 = "23170003;2";
    public static String N113GuardDoor2 = "18200007;2";
    public static String N113GuardDoor1 = "18200006;2";
    public static String N101Envoy1 = "1;36393;-53083;156588;-1896;29256";
    public static int N120CommandCenterDoor4 = 23160006;
    public static int N120CommandCenterDoor3 = 23160007;
    public static int N120CommandCenterDoor2 = 23160005;
    public static String N113FlagPole1 = "-53230,91264,-1924,0,18200500";
    public static int N120CommandCenterDoor1 = 23160004;
    public static int N119CommandCenterDoor4 = 22230004;
    public static int N119CommandCenterDoor3 = 22230005;
    public static int N119CommandCenterDoor2 = 22230007;
    public static int N119CommandCenterDoor1 = 22230006;
    public static int N106CommandCenterDoor4 = 24190008;
    public static String N116GuardDoor4 = "22200002;1";
    public static int N106CommandCenterDoor3 = 24190009;
    public static String N116GuardDoor3 = "22200003;1";
    public static int N106CommandCenterDoor2 = 24190007;
    public static String N116GuardDoor2 = "22200008;2";
    public static int N106CommandCenterDoor1 = 24190006;
    public static String N111Commander3 = "108218,-142186,-2920,8389,36007,36025";
    public static String N116GuardDoor1 = "22200009;2";
    public static String N111Commander2 = "109526,-139713,-2928,61877,36005,36022";
    public static String N111Commander1 = "109855,-142631,-2672,65536,36006,36028";
    public static String N119Envoy2 = "3;36449;72840;186232;-2424;21799";
    public static String N119Envoy1 = "6;36450;72984;186328;-2424;21799";
    public static String N106FlagPole1 = "154872,55325,-2357,0,24190500";
    public static String N118Commander4 = "127640,96261,-2096,40820,36252,36280";
    public static String N118Commander3 = "124181,96539,-2110,45796,36253,36275";
    public static String N114Commander3 = "59322,140861,-1723,45442,36114,36132";
    public static String N118Commander2 = "122727,95769,-2114,42214,36250,36272";
    public static String N114Commander2 = "61849,139172,-1728,47429,36112,36129";
    public static String N118Commander1 = "123229,94410,-1858,60450,36251,36278";
    public static String N114Commander1 = "58492,139631,-1472,53634,36113,36135";
    public static String N121GuardDoor2 = "22150005;1";
    public static String N121GuardDoor1 = "22150004;1";
    public static String N103Flag3 = "16787,188241,-2793,9819";
    public static String N108Envoy1 = "6;36400;118728;205080;-3176;0";
    public static String N103Flag2 = "16723,188287,-2793,9819";
    public static String N103Flag1 = "16664,188321,-2793,9819";
    public static String N112Flag3 = "5338,149838,-2758,9819";
    public static int N111CommandCenterDoor4 = 23130004;
    public static String N112Flag2 = "5337,149759,-2758,9819";
    public static int N111CommandCenterDoor3 = 23130005;
    public static String N112Flag1 = "5342,149667,-2758,9819";
    public static int N111CommandCenterDoor2 = 23130003;
    public static int N111CommandCenterDoor1 = 23130002;
    public static String N119Flag3 = "73046,186258,-2450,9819";
    public static String N119Flag2 = "72974,186214,-2450,9819";
    public static String N119Flag1 = "72891,186169,-2450,9819";
    public static String N120Envoy2 = "8;36454;100360;-55400;-488;32768";
    public static String N120Envoy1 = "7;36447;100360;-55240;-488;32768";
    public static String N115FlagPole1 = "11546,95030,-2498,0,20200500";
    public static int N112CommandCenterDoor4 = 20220024;
    public static int N112CommandCenterDoor3 = 20220025;
    public static int N112CommandCenterDoor2 = 20220023;
    public static int N112CommandCenterDoor1 = 20220022;
    public static String N108FlagPole1 = "118443,204941,-2436,0,23240500";
    public static String N111FlagPole1 = "109441,-141212,-2060,0,23130500";
    public static String N101FlagPole1 = "-52802,156500,-1156,0,18220500";
    public static String N102Flag3 = "-22429,219716,-3105,9819";
    public static String N102Flag2 = "-22424,219794,-3105,9819";
    public static String N102Flag1 = "-22429,219886,-3105,9819";
    public static String N109Flag3 = "158963,-70105,-2733,9819";
    public static String N109Flag2 = "158922,-70165,-2733,9819";
    public static int N102CommandCenterDoor4 = 19240008;
    public static String N109Flag1 = "158879,-70239,-2733,9819";
    public static int N102CommandCenterDoor3 = 19240009;
    public static int N102CommandCenterDoor2 = 19240007;
    public static int N102CommandCenterDoor1 = 19240006;
    public static String N101GuardDoor2 = "18220006;1";
    public static String N101GuardDoor1 = "18220007;1";
    public static String N111Flag3 = "109251,-141073,-2826,9819";
    public static String N111Flag2 = "109224,-141146,-2826,9819";
    public static String N111Flag1 = "109206,-141229,-2826,9819";
    public static String N118Flag3 = "125318,95395,-2009,9819";
    public static String N118Flag2 = "125247,95398,-2009,9819";
    public static int N116CommandCenterDoor4 = 22200007;
    public static String N118Flag1 = "125160,95389,-2009,9819";
    public static int N116CommandCenterDoor3 = 22200006;
    public static String N112Envoy2 = "2;36436;5240;149672;-2728;32768";
    public static String N104GuardDoor4 = "23210009;1";
    public static int N116CommandCenterDoor2 = 22200004;
    public static String N108GuardDoor2 = "23240004;1";
    public static String N112Envoy1 = "1;36435;5240;149784;-2728;32768";
    public static String N104GuardDoor3 = "23210008;1";
    public static int N116CommandCenterDoor1 = 22200005;
    public static String N108GuardDoor1 = "23240005;1";
    public static String N104GuardDoor2 = "23210005;2";
    public static String N104GuardDoor1 = "23210004;2";
    public static int N103CommandCenterDoor4 = 20230005;
    public static int N103CommandCenterDoor3 = 20230004;
    public static int N103CommandCenterDoor2 = 20230008;
    public static int N103CommandCenterDoor1 = 20230009;
    public static String N117FlagPole1 = "111363,-15111,-98,0,23170500";
    public static String N120FlagPole1 = "100674,-55322,251,0,23160500";
    public static String N102Commander4 = "-22709,221638,-3200,32315,35693,35721";
    public static String N102Commander3 = "-21511,221530,-3209,40014,35694,35716";
    public static String N102Commander2 = "-22942,218131,-3210,65393,35691,35713";
    public static String N102Commander1 = "-21304,218849,-2958,10251,35692,35719";
    public static String N109Commander4 = "160234,-68609,-2824,42882,35931,35959";
    public static String N109Commander3 = "157287,-70739,-2837,63065,35932,35954";
    public static int N117CommandCenterDoor4 = 23170004;
    public static String N109Commander2 = "158033,-71708,-2839,60546,35929,35951";
    public static String N105Commander3 = "71234,4150,-3008,1339,35800,35818";
    public static int N117CommandCenterDoor3 = 23170005;
    public static String N109Commander1 = "159668,-72216,-2585,32768,35930,35957";
    public static String N105Commander2 = "73745,5487,-3016,56870,35798,35815";
    public static int N117CommandCenterDoor2 = 23170009;
    public static String N105Commander1 = "72397,2903,-2760,28115,35799,35821";
    public static int N117CommandCenterDoor1 = 23170008;
    public static String N112GuardDoor4 = "20220028;1";
    public static String N112GuardDoor3 = "20220027;1";
    public static String N112GuardDoor2 = "20220019;2";
    public static String N112GuardDoor1 = "20220020;2";
    public static String N103FlagPole1 = "16603,188096,-2027,0,20230500";
    public static String N119GuardDoor2 = "22230002;1";
    public static String N119GuardDoor1 = "22230003;1";
    public static String N110Commander4 = "68120,-60756,-2744,44944,35969,35997";
    public static String N115GuardDoor2 = "20200005;1";
    public static String N110Commander3 = "68583,-59694,-2759,42812,35970,35992";
    public static String N115GuardDoor1 = "20200004;1";
    public static String N115Envoy2 = "4;36442;11594;95305;-3270;16384";
    public static String N110Commander2 = "71288,-62271,-2759,12431,35967,35989";
    public static String N115Envoy1 = "2;36441;11471;95305;-3270;16384";
    public static String N110Commander1 = "71256,-60503,-2507,36315,35968,35995";
    public static int N121CommandCenterDoor4 = 22150007;
    public static int N121CommandCenterDoor3 = 22150006;
    public static String N101Flag3 = "-52996,156652,-1922,9819";
    public static int N121CommandCenterDoor2 = 22150002;
    public static String N101Flag2 = "-53025,156563,-1922,9819";
    public static int N121CommandCenterDoor1 = 22150003;
    public static String N101Flag1 = "-53042,156476,-1922,9819";
    public static String N108Flag3 = "118689,204981,-3202,9819";
    public static String N108Flag2 = "118652,205056,-3202,9819";
    public static String N108Flag1 = "118607,205130,-3202,9819";
    public static String N111Envoy1 = "9;36434;109128;-141112;-2800;29413";
    public static String N117Commander4 = "113987,-14772,-960,35098,36214,36242";
    public static String N113Commander4 = "-50825,92355,-2776,40747,36076,36104";
    public static int N107CommandCenterDoor4 = 25190006;
    public static String N117Commander3 = "112474,-13906,-967,45640,36215,36237";
    public static String N113Commander3 = "-54290,92638,-2790,50995,36077,36099";
    public static int N107CommandCenterDoor3 = 25190007;
    public static String N117Commander2 = "109871,-16468,-968,16043,36212,36234";
    public static String N113Commander2 = "-55693,91847,-2795,65163,36074,36096";
    public static int N107CommandCenterDoor2 = 25190009;
    public static String N117Commander1 = "113494,-16067,-716,57343,36213,36240";
    public static String N113Commander1 = "-55251,90477,-2539,44928,36075,36102";
    public static int N107CommandCenterDoor1 = 25190008;
    public static String N120GuardDoor2 = "23160002;1";
    public static String N120GuardDoor1 = "23160003;1";
    public static String N116Commander4 = "80822,88894,-2878,21495,36176,36204";
    public static String N104Envoy1 = "3;36396;126076;123664;-2427;16564";
    public static String N116Commander3 = "80893,90547,-2885,37278,36177,36199";
    public static String N116Commander2 = "77222,91669,-2886,4245,36174,36196";
    public static String N110Flag3 = "70014,-61208,-2655,9819";
    public static String N116Commander1 = "79420,88744,-2633,37604,36175,36202";
    public static String N110Flag2 = "69943,-61177,-2655,9819";
    public static String N110Flag1 = "69861,-61150,-2655,9819";
    public static String N117Flag3 = "111443,-14861,-864,9819";
    public static String N117Flag2 = "111362,-14863,-864,9819";
    public static String N117Flag1 = "111282,-14876,-864,9819";
    public static String N119FlagPole1 = "73087,186022,-1684,0,22230500";
    public static int N108CommandCenterDoor4 = 23240002;
    public static int N108CommandCenterDoor3 = 23240003;
    public static int N108CommandCenterDoor2 = 23240007;
    public static int N108CommandCenterDoor1 = 23240006;
    public static String N121Commander3 = "73730,-94056,-1401,43943,36360,36382";
    public static String N121Commander2 = "70374,-93983,-1401,61344,36357,36379";
    public static String N118Envoy3 = "5;36448;125368;95496;-1976;16384";
    public static String N121Commander1 = "73689,-95435,-1149,3797,36358,36385";
    public static String N118Envoy2 = "3;36444;125272;95496;-1976;16384";
    public static String N118Envoy1 = "4;36446;125144;95496;-1976;16384";
    public static String N112FlagPole1 = "5566,149751,-1992,0,20220500";
    public static String N114Envoy2 = "3;36451;60264;139790;-1592;0";
    public static String N114Envoy1 = "2;36439;60424;139790;-1592;0";
    public static String N105FlagPole1 = "72831,4306,-2148,0,22180500";
    public static String N107Envoy1 = "5;36399;189928;40104;-3248;16384";
    public static String N103Envoy1 = "2;36395;16789;188424;-2766;0";
    public static String N107Flag3 = "190011,40008,-3279,9819";
    public static String N107Flag2 = "189932,40012,-3279,9819";
    public static String N107Flag1 = "189848,40005,-3279,9819";
    public static int N113CommandCenterDoor4 = 18200012;
    public static int N113CommandCenterDoor3 = 18200011;
    public static int N113CommandCenterDoor2 = 18200002;
    public static int N113CommandCenterDoor1 = 18200003;
    public static String N116Flag3 = "79521,91098,-2753,9819";
    public static String N116Flag2 = "79482,91172,-2753,9819";
    public static String N116Flag1 = "79431,91248,-2753,9819";
    public static String N114FlagPole1 = "60288,139474,-857,0,21220500";
    public static String N106Envoy1 = "5;36398;154616;55544;-3096;25700";
    public static String N107GuardDoor4 = "25190011;1";
    public static String N107GuardDoor3 = "25190010;1";
    public static String N107GuardDoor2 = "25190005;2";
    public static String N103GuardDoor2 = "20230007;1";
    public static String N107GuardDoor1 = "25190004;2";
    public static String N103GuardDoor1 = "20230006;1";
    public static String N107FlagPole1 = "189930,39776,-2513,0,25190500";
    public static String N110FlagPole1 = "69848,-61390,-1889,0,22160500";
    public static String N106GuardDoor2 = "24190012;1";
    public static String N106GuardDoor1 = "24190011;1";
    public static String N101Commander3 = "-53969,155388,-2025,10251,35662,35680";
    public static String N101Commander2 = "-52161,157718,-2026,28577,35660,35677";
    public static String N101Commander1 = "-52416,155129,-1774,24970,35661,35683";
    public static int N104CommandCenterDoor4 = 23210006;
    public static int N104CommandCenterDoor3 = 23210007;
    public static int N104CommandCenterDoor2 = 23210011;
    public static int N104CommandCenterDoor1 = 23210010;
    public static String N108Commander3 = "118442,206579,-3306,49389,35900,35918";
    public static String N104Commander4 = "128045,123385,-2536,32768,35762,35790";
    public static String N108Commander2 = "118986,203647,-3306,5058,35898,35915";
    public static String N104Commander3 = "124807,124698,-2555,1440,35763,35785";
    public static String N108Commander1 = "117215,205641,-3054,28439,35899,35921";
    public static String N104Commander2 = "124305,123547,-2557,48278,35760,35782";
    public static String N104Commander1 = "124774,121855,-2303,22796,35761,35788";

    /*************************** siege_territory.properties ***************************/
    public static String InnadrilFlagPos = "116024,249304,-784";
    public static int AdenFlagItemId = 13564;
    public static int DionFlagNpcId = 36573;
    public static int GoddardFlagNpcId = 36578;
    public static int GludioFlagNpcId = 36572;
    public static String GoddardFlagPos = "147464,-48488,-2272";
    public static String SchuttgartFlagPos = "77544,-152552,-544";
    public static String GiranFlagPos = "116712,145096,-2560";
    public static int OrenFlagNpcId = 36575;
    public static int RuneFlagNpcId = 36579;
    public static int GiranFlagNpcId = 36574;
    public static int SchuttgartFlagItemId = 13568;
    public static String OrenFlagPos = "82760,37192,-2288";
    public static int RuneFlagItemId = 13567;
    public static int OrenFlagItemId = 13563;
    public static int GiranFlagItemId = 13562;
    public static int DionFlagItemId = 13561;
    public static String RuneFlagPos = "11704,-49144,-536";
    public static String DionFlagPos = "22072,160520,-2688";
    public static int SchuttgartFlagNpcId = 36580;
    public static String AdenFlagPos = "147448,4632,-336";
    public static int GoddardFlagItemId = 13566;
    public static int InnadrilFlagNpcId = 36577;
    public static String GludioFlagPos = "-18120,109224,-2496";
    public static int InnadrilFlagItemId = 13565;
    public static int GludioFlagItemId = 13560;
    public static int AdenFlagNpcId = 36576;
    public static boolean isOnlySiegeZone = true;

    /*************************** skills.properties ***************************/
    public static boolean DispelDanceSong = false;
    public static boolean PtsBagMacro = false;
    public static boolean PtsSoulShotCast = false;
    public static int PtsBagMacroTime = 1000;
    public static int LimitMDef = 15000;
    public static double RateEpicDefense = 1.;
    public static boolean AttribShowCalc = false;
    public static int LimitAccuracy = 200;
    public static int SkillsCastTimeMin = 500;
    public static boolean AltDeleteSABuffs = false;
    public static int TriggerLimit = 12;
    public static boolean AltDisableSpellbooks = false;
    public static double SkillsChanceCap = 90.;
    public static boolean AutoLearnForgottenSkills = false;
    public static boolean EnableModifySkillDuration = false;
    public static int MaxCP = 150000;
    public static float AltPKDeathRate = 0f;
    public static int LethalRate = 600;
    public static int LimitFame = 100000;
    public static int DeathPenaltyC5RateKarma = 500;
    public static int LimitPDef = 15000;
    public static int LimitAbsorbDam = 100;
    public static int LimitAbsorbDamMp = 100;
    public static boolean SkillsShowChance = true;
    public static boolean OldSkillDelete = false;
    public static int LimitMCritical = 20;
    public static int LimitReflectDam = 100;
    public static int MinPhisAttackSpeed = 333;
    public static boolean BuffSummon = true;
    public static double SkillsChanceMin = 10.;
    public static int BuffLimit = 20;
    public static double SkillsChanceModPvP = 11.;
    public static boolean AltSaveUnsaveable = true;
    public static float BuffTimeModifier = 1f;
    public static int LimitMAtk = 25000;
    public static boolean DeleteNoblesseBlessing = true;
    public static boolean UnstuckSkill = false;
    public static int LimitEvasion = 250;
    public static int LimitPatkSpd = 1500;
    public static int LimitMatkSpd = 1999;
    public static boolean AllowClanSkills = true;
    public static int LimitMove = 250;
    public static int LimitMoveHourse = 250;
    public static int LimitCritical = 500;
    public static float AbsorbDamageModifier = 1f;
    public static boolean AltAllPhysSkillsOverhit = false;
    public static String SkillDurationList = "";
    public static boolean DebugStatLimits = false;
    public static double SkillsChanceMod = 11.;
    public static int MaxHP = 60000;
    public static boolean MultiProfa = false;
    public static float SongDanceTimeModifier = 1f;
    public static boolean EnableAltDeathPenalty = false;
    public static int DeathPenaltyC5Chance = 10;
    public static boolean AutoLearnSkills = false;
    public static float ClanHallBuffTimeModifier = 1f;
    public static boolean ChaoticCanUseScrollOfRecovery = true;
    public static double RateRaidDefense = 1.;
    public static int DebuffLimit = 8;
    public static double RateEpicAttack = 1.;
    public static int DeathPenaltyC5RateExpPenalty = 1;
    public static int SongLimit = 12;
    public static double SkillsChancePowPvP = 0.5;
    public static int AutoLearnSkillsMaxLevel = 85;
    public static boolean ManahealSpSBonus = false;
    public static int LimitCriticalDamage = 2000;
    public static int BaseCriticalDamage = 100;
    public static boolean AltRemoveSkillsOnDelevel = true;
    public static int MaxMP = 60000;
    public static boolean AllowLearnTransSkillsWOQuest = false;
    public static boolean AltShowSkillReuseMessage = true;
    public static int LimitPatk = 20000;
    public static double SkillsChancePow = 0.5;
    public static boolean EnableDeathPenaltyC5 = true;
    public static double RateRaidAttack = 1.;
    public static int[] SkillsS80andS84Sets = {3416, 8210, 3354, 8211, 3355, 8212, 3356, 8213, 3357, 8214, 3412, 8202, 3348, 8203, 3349, 8204, 3350, 8205, 3351, 8206, 3413, 8207, 3352, 8208, 3353, 8209, 3414, 8215, 3415, 8216, 3420, 8217, 3420, 8218, 3645, 8229, 3646, 8230, 3647, 8231, 3648, 8232, 3636, 8219, 3637, 8220, 3638, 8221, 3639, 8222, 3640, 8223, 3641, 8224, 3642, 8225, 3643, 8226, 3644, 8227, 3805, 8228, 8284, 8286, 8288, 8302, 8304, 8306, 8403, 8404, 8405, 8412, 8413, 8414, 8400, 8401, 8402, 8409, 8410, 8411, 8397, 8398, 8399, 8406, 8407, 8408};
    public static boolean ChecksForTeam = false;
    public static boolean AltFormulaCastBreak = false;
    public static boolean SetFlagSkillsOnlyLord = true;
    public static double DotModifer = 0.666;
    public static boolean InvullOnlyTest = false;
    public static int[] SkillShareHealerBishop = {1013, 32, 1087, 3, 1033, 3, 1257, 3, 1059, 3, 1268, 4, 1189, 3, 1303, 2, 1243, 6, 1259, 4, 1273, 13, 1050, 2, 1255, 2, 1304, 3, 1393, 3, 1397, 3, 1240, 3, 1242, 3, 1531, 7, 1392, 3, 1539, 4};
    public static int[] SkillShareHealerElder = {1077, 3, 1075, 15, 1018, 3, 1218, 33, 1254, 6, 1049, 14, 1034, 13, 1258, 4, 1042, 12, 1311, 6, 1396, 10, 1399, 5, 1402, 5, 1418, 1, 1271, 1, 1307, 3, 1240, 3, 1059, 3, 1268, 4, 1242, 3, 1531, 7, 1392, 3, 1539, 4, 1189, 3};
    public static int[] SkillShareHealerSilenElder = {1043, 1, 1044, 3, 1075, 15, 1087, 3, 1033, 3, 1257, 3, 1218, 33, 1254, 6, 1049, 14, 1034, 13, 1258, 4, 1042, 12, 1311, 6, 1396, 10, 1399, 5, 1402, 5, 1418, 1, 1271, 1, 1307, 3, 1243, 6, 1259, 4, 1273, 13, 1050, 2, 1255, 2, 1304, 3, 1393, 3, 1397, 3, 1401, 11, 1020, 27, 1028, 19, 1394, 10, 1400, 10};
    public static int DebuffFormulaType = 0;
    public static boolean OlympiadBreakCastMod = false;
    public static double OlympiadBreakCastModValue = 1;
    public static int[][] SkillEnchantCostOffensive = /** Цена заточки атакующих скиллов */
            {
                    {}, //
                    {93555, 635014}, // 1
                    {93555, 635014}, // 2
                    {93555, 635014}, // 3
                    {141183, 666502}, // 4
                    {141183, 666502}, // 5
                    {141183, 666502}, // 6
                    {189378, 699010}, // 7
                    {189378, 699010}, // 8
                    {189378, 699010}, // 9
                    {238140, 749725}, // 10
                    {238140, 749725}, // 11
                    {238140, 749725}, // 12
                    {287469, 896981}, // 13
                    {287469, 896981}, // 14
                    {287469, 896981}, // 15
                    {337365, 959562}, // 16
                    {337365, 959562}, // 17
                    {337365, 959562}, // 18
                    {387828, 1002822}, // 19
                    {387828, 1002822}, // 20
                    {387828, 1002822}, // 21
                    {438858, 1070184}, // 22
                    {438858, 1070184}, // 23
                    {438858, 1070184}, // 24
                    {496601, 1142024}, // 25, цифра неточная
                    {496601, 1142024}, // 26, цифра неточная
                    {496601, 1142024}, // 27, цифра неточная
                    {561939, 1218716}, // 28, цифра неточная
                    {561939, 1218716}, // 29, цифра неточная
                    {561939, 1218716}, // 30, цифра неточная
            };

    public static int[][] SkillEnchantCostBuff = /** Цена заточки неатакующих скиллов */
            {
                    {}, //
                    {51975, 352786}, // 1
                    {51975, 352786}, // 2
                    {51975, 352786}, // 3
                    {78435, 370279}, // 4
                    {78435, 370279}, // 5
                    {78435, 370279}, // 6
                    {105210, 388290}, // 7
                    {105210, 388290}, // 8
                    {105210, 388290}, // 9
                    {132300, 416514}, // 10
                    {132300, 416514}, // 11
                    {132300, 416514}, // 12
                    {159705, 435466}, // 13
                    {159705, 435466}, // 14
                    {159705, 435466}, // 15
                    {187425, 466445}, // 16
                    {187425, 466445}, // 17
                    {187425, 466445}, // 18
                    {215460, 487483}, // 19
                    {215460, 487483}, // 20
                    {215460, 487483}, // 21
                    {243810, 520215}, // 22
                    {243810, 520215}, // 23
                    {243810, 520215}, // 24
                    {272475, 542829}, // 25
                    {272475, 542829}, // 26
                    {272475, 542829}, // 27
                    {304500, 566426}, // 28, цифра неточная
                    {304500, 566426}, // 29, цифра неточная
                    {304500, 566426}, // 30, цифра неточная
            };
    public static byte SkillReuseType = 0;
    public static int NextAtackDelayMod = 500000;

    /*************************** spoil.properties ***************************/
    public static int AltManorApproveMin = 00;
    public static int BasePercentChanceOfSowingAltSuccess = 20;
    public static boolean AllowManor = true;
    public static int DiffSeedMobPenalty = 5;
    public static int BasePercentChanceOfHarvestingSuccess = 90;
    public static float BasePercentChanceOfSpoilSuccess = 78f;
    public static int AltManorRefreshMin = 00;
    public static int MinDiffPlayerMob = 5;
    public static int AltManorApproveTime = 6;
    public static int MinDiffSeedMob = 5;
    public static float MinimumPercentChanceOfSpoilSuccess = 1f;
    public static int AltManorRefreshTime = 20;
    public static int AltManorMaintenancePeriod = 360000;
    public static int DiffPlayerMobPenalty = 5;
    public static int BasePercentChanceOfSowingSuccess = 90;

    /*************************** telnet.properties ***************************/
    public static boolean EnableTelnet = false;
    public static String StatusPW = "somePass";
    public static String ListOfHosts = "127.0.0.1,localhost";
    public static int StatusPort = 12345;

    /*************************** territorywar.properties ***************************/
    public static int MinTerritoryBadgeForStriders = 50;
    public static int MinTerritoryBadgeForBigStrider = 80;
    public static int TerritoryWarSiegeHourOfDay = 20;
    public static int TerritoryWarSiegeDayOfWeek = 7;
    public static int WarLength = 120;
    public static int ReturnDropedTerritoryFlag = -1;
    public static int TerritoryFlagCountOwn = Integer.MAX_VALUE;
    public static int TerritoryFlagMinDistToDrop = 0;
    public static int TerritoryFlagMaxDistToDrop = 100;

    /*************************** loginserver.properties ***************************/
    public static int LoginTryBeforeBan = 20;
    public static int LoginTryCheckDuration = 300;
    public static int LoginTryBanDuration = 600;

    public static int LoginWatchdogTimeout = 15000;
    public static boolean ShowLicence = true;
    //public static boolean AcceptNewGameServer = true;
    public static boolean FakeLogin = false;
    public static boolean Debug = false;
    public static String AnameTemplate = "[A-Za-z0-9]{3,14}";
    public static int BlowFishKeys = 20;
    public static String DefaultPasswordEncoding = "Whirlpool";
    public static int IpUpdateTime = 15;
    public static String LegacyPasswordEncoding = "SHA1;DES";
    public static String InternalIpList = "127.0.0.1,192.168.0.0-192.168.255.255,10.0.0.0-10.255.255.255,172.16.0.0-172.16.31.255";
    public static int RSAKeyPairs = 10;
    public static String DoubleWhirlpoolSalt = "blablabla";
    public static boolean ComboMode = false;
    public static String LoginserverHostname = "127.0.0.1";
    public static int LoginserverPort = 2106;
    public static boolean AutoCreateAccounts = false;
    public static boolean AllowOldAuth = false;
    public static int LoginserverId = 0;
    public static boolean AltAdvIPSystem = false;
    public static boolean SkipBannedIp = false;
    public static boolean DEBUG_LS_GS = false;

    /*************************** fightclub.properties ***************************/
    public static boolean FightClubEnabled = false;
    public static int MinimumLevel = 1;
    public static int MaximumLevel = 85;
    public static int MaximumLevelDifference = 10;
    public static String AllowedItems = "57,4037";
    public static long MaxItemsCount = 50000000000L;
    public static int RatesOnPage = 10;
    public static int ArenaTeleportDelay = 5;
    public static boolean CancelBuffs = true;
    public static boolean UnsummonPets = true;
    public static boolean UnsummonSummons = false;
    public static boolean RemoveClanSkills = false;
    public static boolean RemoveHeroSkills = false;
    public static int TimeToPreparation = 30;
    public static int TimeToDraw = 300;
    public static boolean AllowDraw = true;
    public static int TimeToBack = 10;
    public static boolean AnnounceRate = true;
    public static int[] AnnounceRateItem = {};
    public static long[] AnnounceRateItemCount = {};
    public static boolean FightClubBattleUseBuffer = false;


    /*************************** fantasyIsland.properties ***************************/
    public static boolean EnableBlockCheckerEvent = true;
    public static boolean HBCEFairPlay = true;
    public static int BlockCheckerMinTeamMembers = 2;
    public static int BlockCheckerRateCoinReward = 1;
    /*************************** (CCPGuard)protected.properties ***************************/
    public static String UpProtectedIPs = "";
    public static boolean EnableProtect = true;
    public static int ServerConst = 0;
    public static int AllowedWindowsCount = 99;
    public static int ProtectProtocolVersion = 777;
    public static boolean KickWithEmptyHWID = true;
    public static boolean KickWithLastErrorHWID = false;
    public static boolean EnableHWIDLock = false;
    public static boolean EnableGGSystem = true;
    public static boolean ProtectDebug = false;
    public static boolean HwidProblemResolved = false;
    public static long GGSendInterval = 60000;
    public static long GGRecvInterval = 8000;
    public static long GGTaskInterval = 5000;
    public static int TotalPenaltyPoint = 10;
    public static String ShowHtml = "none";
    public static int PunishmentIllegalSoft = 1;
    public static int PenaltyIG = 10;
    public static int PenaltyBot = 10;
    public static int PenaltyL2phx = 10;
    public static int PenaltyL2Control = 10;
    public static int PenaltyConsoleCMD = 1;
    public static String ServerTitle = null;
    public static int ServerTitlePosX = 260;
    public static int ServerTitlePosY = 8;
    public static String ServerTitleColor = "0xFF00FF00";
    public static int OnlinePacketTime = 0;
    public static int OnlinePacketPosX = 260;
    public static int OnlinePacketPosY = 20;
    public static String OnlinePacketColor = "0xFF00FF00";
    public static int PingPacketTime = 0;
    public static int PingPacketPosX = 260;
    public static int PingPacketPosY = 32;
    public static String PingPacketColor = "0xFF00FF00";
    public static boolean EnablePortKnock = false;
    public static String PortKnockCommand = "ipset -A %ip% game_ok";
    public static int UpdaterCheck = 0;
    public static int UpdaterOpcode = 0;
    public static int CCPGuardSize2 = 48;
    public static int PenaltyBitsError = 0;
    public static int PenaltyHooker = 10;
    public static int PenaltyAika = 10;

    /*************************** gvg.properties ***************************/
    public static int[] GvG_StartTimeList = {};
    public static int GvG_MinLevel = 80;
    public static int GvG_MaxLevel = 85;
    public static int GvG_GroupLimit = 100;
    public static int GvG_MinPatyMembers = 6;
    public static int GvG_RegTime = 10;
    public static int GvG_ItemReward = 13067;
    public static int GvG_ItemRewardCount = 30;
    public static int GvG_AddFame = 500;
    public static boolean GvG_Enable = false;
    public static boolean GvG_HWID = false;

    /*************************** marcket.properties ***************************/
    public static boolean EnableMarcket = false;
    public static boolean EnableMarcketRegAcc = false;

    /*************************** PTS Config ***************************/
    public static String NotSpawnMaker = "";
    public static boolean EnablePtsSpawnEngine = false;
    public static boolean EnableCustomLoaded = false;

    public static int PtsAiTaskMangerCount = 1;
    public static int TaskReTime = 1000;
    public static String SpawnListData = develop ? "data/pts_spawn/npcpos.txt" : "./data/pts_spawn/npcpos.txt";

    /*************************** Nevit and Recomendation Config ***************************/
    public static int CurrentPointLvlUp = 1200;
    public static int CurrentPointVitalityUp = 1000;
    public static int CurrentPointMinuteUp = 144;

    /*************************** License Config ***************************/
    public static final int CoreRevision = 2800;
    private static int LicenseRevision = 0;

    /*************************** Event Victorina **************************/
    public static boolean Victorina_Enable = false;
    public static boolean Victorina_Remove_Question = false;
    public static boolean Victorina_Remove_Question_No_Answer = false;
    public static int Victorina_Start_Time = 16;
    public static int Victorina_Work_Time = 2;
    public static int Victorina_Time_Answer = 1;
    public static int Victorina_Time_Pause = 1;
    public static String Victorina_Reward_First = "57,1,100;57,2,100;";
    public static String Victorina_Reward_Other = "57,1,100;57,2,100;";

    /*************************** Event LastGrup **************************/
    public static int LGTime = 5;
    public static int LGPlayerCount = 3;
    public static long LGEndTime = 300000;
    public static boolean LGHWID = true;
    public static boolean LGAllowUsePotions = false;
    public static boolean LGRemoveClanSkills = true;
    public static boolean LGRemoveHeroSkills = true;
    public static boolean LGCancelBuffs = true;
    public static boolean LGUnsummonPets = true;
    public static boolean LGUnsummonSummons = true;

    /*************************** Rang Config **************************/
    public static boolean RangEnable = false;
    public static boolean RangEnableTitle = true;
    public static int RangReloadInfoInterval = 3600;
    public static int[] Rang0SkillList = {};
    public static int[] Rang1SkillList = {};
    public static int[] Rang2SkillList = {};
    public static int[] Rang3SkillList = {};
    public static int[] Rang4SkillList = {};
    public static int[] Rang5SkillList = {};
    public static int[] Rang6SkillList = {};
    public static int[] Rang7SkillList = {};
    public static int[] Rang8SkillList = {};
    public static int[] Rang9SkillList = {};
    public static int[] Rang10SkillList = {};
    public static int[] Rang11SkillList = {};
    public static int[] Rang12SkillList = {};
    public static int[] Rang13SkillList = {};
    public static int[] Rang14SkillList = {};
    public static int[] Rang15SkillList = {};
    public static int[] Rang16SkillList = {};
    public static int[] Rang17SkillList = {};

    public static float[] RangPercentTransferPointChar = {1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f};
    public static float[] RangPercentTransferPointMob = {1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f};
    public static float[] RangPercentAddPointMob = {1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f};

    /*************************** Other Fields ***************************/
    public static int LicenseRevision() {
        return LicenseRevision;
    }

    public static void LicenseRevision(int value) {
        LicenseRevision = value;
    }

    public static String ExternalHostname() {
        return ExternalHostname;
    }

    public static void ExternalHostname(String value) {
        ExternalHostname = value;
    }

    /*************************** ...Здесь у нас разное дерьмо от клиентов, их примочки и фичи... ***************************/
    public static boolean AllowCustomHtmlCache = false;
    public static boolean AllowCustomCommunityBoard = true;
    public static boolean AllowCBBuffer = true;
    public static boolean AllowCBBufferInInstance = false;
    public static int CBBufferCountEffects = 30;
    public static int CBBufferCountEffectsInColumn = 10;
    public static long[] CBBufferPriceForBuff = {57, 1000};
    public static int CBBufferMaxGroups = 5;
    public static boolean DropMob = false;
    public static int MinVersion = -195999099;
    public static int MaxVersion = 195999099;
    public static int MaxAllowedInstances = 99;
    public static int HWIDBan = 14;
    public static boolean UseDefaultEncoder = false;
    public static float CanByTradeItemPA = -1f;
    public static int[] CanByTradeItemPA_Price = new int[0];
    public static boolean AffordBonus = false;
    public static boolean L2NameBonus = false;
    public static int[] CanNotByTradeItemPA = {10280, 10281, 10282, 10283, 10284, 10285, 10286, 10287, 10288, 10289, 10290, 10291, 10292, 10293, 10294, 10612, 15307, 15308, 15309};

    /*************************** Конфиги Турнира ***************************/
    public static String[] Tournament_StartRegData = {"01/01/1970 01:00:00"}; // dd/MM/yyyy HH:mm:ss
    public static String[] Tournament_EndRegData = {"01/01/1970 01:00:00"}; // dd/MM/yyyy HH:mm:ss
    public static String[] Tournament_StartTournData = {"01/01/1970 01:00:00"}; // dd/MM/yyyy HH:mm:ss
    public static int Tournament_RegItemId = 57;
    public static long Tournament_RegItemCount = 0;
    public static int Tournament_UPWinPoint = 2;
    public static int Tournament_UPLoosPoint = 1;
    public static int Tournament_DwnWinPoint = 1;
    public static int Tournament_DwnLoosPoint = 1;
    public static int Tournament_EndTime = 600000;
    public static int Tournament_TimeToStart = 5;
    public static boolean Tournament_AutoReg = true;
    public static boolean Tournament_Debug = false;
    public static boolean Tournament_Enable = false;
    // 1. Сколько давать за победу/поражение в винерах/лузерах в регулярном турнире.
    // 2. Сколько давать за победу/поражение в винерах/лузерах в Гран-при.
    public static int[] Tournament_TeamMemberCount = {23};
    public static int[] Tournament_TeamMemberFait = {3};
    public static int[] Tournament_TeamMemberFaitMin = {3};
    public static int[] Tournament_TopItemId = {};
    public static long[] Tournament_TopItemCount = {};
    public static int[] Tournament_SecondPlaceItemId = {};
    public static long[] Tournament_SecondPlaceItemCount = {};
    public static int[] Tournament_ThirdPlaceItemId = {};
    public static long[] Tournament_ThirdPlaceItemCount = {};
    public static boolean Tournament_OlympiadItems = false;
    public static int Tournament_CanResurect = 2;
    public static boolean TournamentBattleUseBuffer = false;
    public static int Tournament_WaitTime = 5;
    public static int Tournament_Time = 5;
    public static int Tournament_NextBattleTime = 1;
    public static int[][] Tournament_StartTime = {{-1}};
    public static int[] Tournament_ZoneList = {};
    public static int[][] Tournament_MagicBuffList = {{1323, 1}};
    public static int[][] Tournament_PhisicBuffList = {{1323, 1}};
    public static int Tournament_DanceAndSongTime = 300;
    public static int Tournament_ResetReuseSkillsTime = 900;
    public static int Tournament_BuffTime = 300;
    public static int[] Tournament_ForbiddenItems = {6406, 6407, 5234, 5235, 5236, 5237, 8664, 8665, 8666, 5238, 5239, 5240, 5241, 8667, 8668, 8669, 8670, 5242, 5243, 5244, 5245, 5246, 5247, 5248, 8671, 8672, 8673, 8674, 8675, 8676, 8677};
    public static boolean Tournament_Cancel = true;
    public static boolean Tournament_Root = true;
    public static long[] Tournament_Reward = {57, 20, 4037, 1};
    public static long[] Tournament_RewardWinner1 = {57, 20, 4037, 1};
    public static long[] Tournament_RewardWinner2 = {57, 20, 4037, 1};
    public static long[] Tournament_RewardKillPlayer = {57, 20};
    public static long[] Tournament_AddItem = {57, 1};
    public static boolean Tournament_DellHeroSkill = false;
    public static boolean Tournament_UseBuffer = false;
    public static boolean Tournament_CommunityBlock = false;
    public static boolean Tournament_SetTeam = false;
    public static int Tournament_MaxEnchantWeapon = 65536;
    public static int Tournament_MaxEnchantArmor = 65536;
    public static int Tournament_MaxEnchantJewel = 65536;
    public static int[] Tournament_ItemLimit = {};


    public static boolean BotDebug = false;
    public static boolean BotEnable = false;
    public static boolean Bot21Enable = false;
    public static int BotCount = 800;
    public static int BotCountStart = 100;
    public static int BotInterval = 10;

    /*************************** L2CCCP ***************************/
    public static int BBS_ENCHANT_ITEM = 4356;
    public static int[] BBS_ENCHANT_MAX = {25, 25, 25};
    public static int[] BBS_WEAPON_ENCHANT_LVL = {5};
    public static int[] BBS_ARMOR_ENCHANT_LVL = {5};
    public static int[] BBS_JEWELS_ENCHANT_LVL = {5};
    public static int[] BBS_ENCHANT_PRICE_WEAPON = {5};
    public static int[] BBS_ENCHANT_PRICE_ARMOR = {5};
    public static int[] BBS_ENCHANT_PRICE_JEWELS = {5};

    public static int BBS_ENCHANT_WEAPON_ATTRIBUTE_MAX = 25;
    public static int BBS_ENCHANT_ARMOR_ATTRIBUTE_MAX = 25;
    public static int[] BBS_ENCHANT_ATRIBUTE_LVL_WEAPON = {25};
    public static int[] BBS_ENCHANT_ATRIBUTE_LVL_ARMOR = {25};
    public static int[] BBS_ENCHANT_ATRIBUTE_PRICE_ARMOR = {25};
    public static int[] BBS_ENCHANT_ATRIBUTE_PRICE_WEAPON = {25};
    public static boolean BBS_ENCHANT_ATRIBUTE_PVP = true;

    public static boolean BBS_ENCHANT_HEAD_ATTRIBUTE = true;
    public static boolean BBS_ENCHANT_CHEST_ATTRIBUTE = true;
    public static boolean BBS_ENCHANT_LEGS_ATTRIBUTE = true;
    public static boolean BBS_ENCHANT_GLOVES_ATTRIBUTE = true;
    public static boolean BBS_ENCHANT_FEET_ATTRIBUTE = true;
    public static String[] BBS_ENCHANT_GRADE_ATTRIBUTE = {"NG:NO", "D:NO", "C:NO", "B:NO", "A:ON", "S:ON", "S80:ON", "S84:ON"};

    public static boolean BBS_ENCHANT_WEAPON_ATTRIBUTE = true;
    public static boolean BBS_ENCHANT_SHIELD_ATTRIBUTE = false;

    public static boolean OnlyAcademicItem = true;

    public static int[] WeaponAugmentOptionList = {16115, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110};
    public static int[] JewelAugmentOptionList = {16115, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110, 16116, 16117, 16118, 16119, 16120, 16114, 16113, 16112, 16111, 16110};

    public static int[] WeaponAugmentOptionItemId = {4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037};
    public static int[] JewelAugmentOptionItemId = {4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037, 4037};
    public static long[] WeaponAugmentOptionCount = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    public static long[] JewelAugmentOptionCount = {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

    public static int[] WeaponAugmentOptionItemId1 = {57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57};
    public static int[] JewelAugmentOptionItemId1 = {57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57};
    public static long[] WeaponAugmentOptionCount1 = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    public static long[] JewelAugmentOptionCount1 = {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

    public static int[] WeaponAugmentOptionItemId2 = {5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537};
    public static int[] JewelAugmentOptionItemId2 = {5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537, 5537};
    public static long[] WeaponAugmentOptionCount2 = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    public static long[] JewelAugmentOptionCount2 = {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};


    public static int WeaponAugmentOptionStat = 10934;
    public static int JewelAugmentOptionStat = 10934;
    public static boolean AugmentOldVer = false;

    /*************************** Ивент, защита долбаёба ***************************/
    public static int[] POC_ = {0};

    public static boolean POC_Enable = false; // Включить ивент ?
    public static int[][] POC_MobTide = {{0}}; // Потоки мобов.
    public static int[][] POC_MobTideLoc = {{0, 0, 0}}; // Координаты спауна мобов...
    public static int[] POC_MobTideCount = {0}; // Количество мобов.
    public static int[] POC_MobTideHp = {0}; // Количество ХП у мобов с определенной волны.
    public static int[] POC_MobTidePAtk = {0}; // Количество PAtk у мобов с определенной волны.
    public static int[] POC_NpcDefenseLoc = {0, 0, 0}; // Координаты спауна НПСа которого нужно защитить.
    public static int[] POC_PlayerLoc = {0, 0, 0}; // Координаты спауна игроков.
    public static int[] POC_SaleNpcLoc = {0, 0, 0}; // Координаты спауна Магазина со шмотом.
    public static int[] POC_ItemKillId = {0}; // Награда за убийство ID.
    public static int[] POC_ItemId = {0}; // Награда ID.
    public static long[] POC_ItemKillCount = {0}; // Награда за убийство количество.
    public static long[] POC_ItemCount = {0}; // Награда количество.
    public static long[] POC_MobTideInterval = {0}; // Задержка спауна мобов в мс.
    public static long POC_RestoreMpItemCount = 0;
    public static int POC_NpcDefenseId = 0; // ID НПса которого нужно защитить.
    public static int POC_NpcSaleId = 0; // ID НПСа для продажи шмота.
    public static int POC_NpcDefenseHp = 0; // Количество ХП у НПса которого нужно защитить.
    public static int POC_EndTime = 10; // Время ивента. В минутах.
    public static int POC_StartTime = 10; // Время до старта ивента...
    public static int POC_TimeToBatle = 10; // Время до сапуна мобов...
    public static int POC_TimeInTide = 20; // Время между волнами
    public static int POC_PartyCount = 3; // Количество игроков в пати для частвия в ивенте.
    public static int POC_PlayerCount = 3; // Количество игроков в команде, для участвия в ивенте.
    public static int POC_RestoreMpItemId = 0;
    public static String[] POC_MobTideAI = {"DefaultAI"}; // АИ мобов для каждой волны.
    public static int[] POC_StartTimeList = {-1};
    /*************************** Мульта ***************************/
    public static boolean Multi_Enable = false;
    public static boolean MultiSub_Enable = false;
    public static boolean Multi_Enable2 = false;
    public static boolean Multi_Enable3 = false;
    public static boolean Multi_AddSubOnlyTown = false;
    public static boolean Multi_DeleteSubInList = false;
    public static boolean Multi_AddBaseClass = false;

    public static byte Multi_StartSubLevel = 1;
    public static byte Multi_NextSubLevel = 78;
    public static int[][] Multi_ItemIdAdd = new int[0][0];
    public static long[][] Multi_ItemIdAddCount = new long[0][0];
    public static int[][] Multi_ItemId = {{57}, {57}, {57}};
    public static long[][] Multi_ItemCount = {{1}, {1}, {1}};
    public static int[] Multi_NextSubTime = {0, 0, 0};
    public static int[] Multi_AddItemId = {};
    public static long[] Multi_AddItemCount = {};
    public static int Multi_SkillDeleteItemId = 57;
    public static long Multi_SkillDeleteItemCount = 5000000;

    public static String Multi_AddItemClass88 = "0:0";
    public static String Multi_AddItemClass89 = "0:0";
    public static String Multi_AddItemClass90 = "0:0";
    public static String Multi_AddItemClass91 = "0:0";
    public static String Multi_AddItemClass92 = "0:0";
    public static String Multi_AddItemClass93 = "0:0";
    public static String Multi_AddItemClass94 = "0:0";
    public static String Multi_AddItemClass95 = "0:0";
    public static String Multi_AddItemClass96 = "0:0";
    public static String Multi_AddItemClass97 = "0:0";
    public static String Multi_AddItemClass98 = "0:0";
    public static String Multi_AddItemClass99 = "0:0";
    public static String Multi_AddItemClass100 = "0:0";
    public static String Multi_AddItemClass101 = "0:0";
    public static String Multi_AddItemClass102 = "0:0";
    public static String Multi_AddItemClass103 = "0:0";
    public static String Multi_AddItemClass104 = "0:0";
    public static String Multi_AddItemClass105 = "0:0";
    public static String Multi_AddItemClass106 = "0:0";
    public static String Multi_AddItemClass107 = "0:0";
    public static String Multi_AddItemClass108 = "0:0";
    public static String Multi_AddItemClass109 = "0:0";
    public static String Multi_AddItemClass110 = "0:0";
    public static String Multi_AddItemClass111 = "0:0";
    public static String Multi_AddItemClass112 = "0:0";
    public static String Multi_AddItemClass113 = "0:0";
    public static String Multi_AddItemClass114 = "0:0";
    public static String Multi_AddItemClass115 = "0:0";
    public static String Multi_AddItemClass116 = "0:0";
    public static String Multi_AddItemClass117 = "0:0";
    public static String Multi_AddItemClass118 = "0:0";
    public static String Multi_AddItemClass119 = "0:0";
    public static String Multi_AddItemClass120 = "0:0";
    public static String Multi_AddItemClass121 = "0:0";
    public static String Multi_AddItemClass122 = "0:0";
    public static String Multi_AddItemClass123 = "0:0";
    public static String Multi_AddItemClass124 = "0:0";
    public static String Multi_AddItemClass125 = "0:0";
    public static String Multi_AddItemClass126 = "0:0";
    public static String Multi_AddItemClass127 = "0:0";
    public static String Multi_AddItemClass128 = "0:0";
    public static String Multi_AddItemClass129 = "0:0";
    public static String Multi_AddItemClass130 = "0:0";
    public static String Multi_AddItemClass131 = "0:0";
    public static String Multi_AddItemClass132 = "0:0";
    public static String Multi_AddItemClass133 = "0:0";
    public static String Multi_AddItemClass134 = "0:0";
    public static String Multi_AddItemClass135 = "0:0";
    public static String Multi_AddItemClass136 = "0:0";

    public static int[] Multi_NoDelleteSkill = {0};

    public static String Multi_AddSkillClass88 = "0:0";
    public static String Multi_AddSkillClass89 = "0:0";
    public static String Multi_AddSkillClass90 = "0:0";
    public static String Multi_AddSkillClass91 = "0:0";
    public static String Multi_AddSkillClass92 = "0:0";
    public static String Multi_AddSkillClass93 = "0:0";
    public static String Multi_AddSkillClass94 = "0:0";
    public static String Multi_AddSkillClass95 = "0:0";
    public static String Multi_AddSkillClass96 = "0:0";
    public static String Multi_AddSkillClass97 = "0:0";
    public static String Multi_AddSkillClass98 = "0:0";
    public static String Multi_AddSkillClass99 = "0:0";
    public static String Multi_AddSkillClass100 = "0:0";
    public static String Multi_AddSkillClass101 = "0:0";
    public static String Multi_AddSkillClass102 = "0:0";
    public static String Multi_AddSkillClass103 = "0:0";
    public static String Multi_AddSkillClass104 = "0:0";
    public static String Multi_AddSkillClass105 = "0:0";
    public static String Multi_AddSkillClass106 = "0:0";
    public static String Multi_AddSkillClass107 = "0:0";
    public static String Multi_AddSkillClass108 = "0:0";
    public static String Multi_AddSkillClass109 = "0:0";
    public static String Multi_AddSkillClass110 = "0:0";
    public static String Multi_AddSkillClass111 = "0:0";
    public static String Multi_AddSkillClass112 = "0:0";
    public static String Multi_AddSkillClass113 = "0:0";
    public static String Multi_AddSkillClass114 = "0:0";
    public static String Multi_AddSkillClass115 = "0:0";
    public static String Multi_AddSkillClass116 = "0:0";
    public static String Multi_AddSkillClass117 = "0:0";
    public static String Multi_AddSkillClass118 = "0:0";
    public static String Multi_AddSkillClass119 = "0:0";
    public static String Multi_AddSkillClass120 = "0:0";
    public static String Multi_AddSkillClass121 = "0:0";
    public static String Multi_AddSkillClass122 = "0:0";
    public static String Multi_AddSkillClass123 = "0:0";
    public static String Multi_AddSkillClass124 = "0:0";
    public static String Multi_AddSkillClass125 = "0:0";
    public static String Multi_AddSkillClass126 = "0:0";
    public static String Multi_AddSkillClass127 = "0:0";
    public static String Multi_AddSkillClass128 = "0:0";
    public static String Multi_AddSkillClass129 = "0:0";
    public static String Multi_AddSkillClass130 = "0:0";
    public static String Multi_AddSkillClass131 = "0:0";
    public static String Multi_AddSkillClass132 = "0:0";
    public static String Multi_AddSkillClass133 = "0:0";
    public static String Multi_AddSkillClass134 = "0:0";
    public static String Multi_AddSkillClass135 = "0:0";
    public static String Multi_AddSkillClass136 = "0:0";

    /*************************** Ивент, дотянуться до небес ***************************/
    public static int RH_StartTime = 10;
    public static int RH_PartyCount = 2;
    public static int RH_PlayerCount = 1;
    public static int RH_EndTime = 10;
    public static int RH_TimeToBatle = 60;
    public static int RH_DanceAndSongTime = 300;
    public static int RH_BuffTime = 300;
    public static int RH_MinPlayerLevel = 76;
    public static int RH_MaxPlayerLevel = 85;
    public static int RH_MinPlayerCount = 2;
    public static int[] RH_StartTimeList = {-1};
    public static int[] RH_ItemId = {0}; // Награда ID.
    public static long[] RH_ItemCount = {0}; // Награда количество.
    public static int[][] RH_MagicBuff = {{264, 1}, {267, 1}, {268, 1}, {273, 1}, {276, 1}, {304, 1}, {349, 1}, {363, 1}, {365, 1}, {529, 1}, {530, 1}, {1035, 1}, {1078, 1}, {1085, 1}, {1259, 1}, {1303, 1}, {1307, 1}, {1352, 1}, {1364, 1}, {1389, 1}, {1397, 1}, {1413, 1}, {1461, 1}, {1500, 1}, {1501, 1}, {1503, 1}, {1504, 1}, {1517, 1}, {4703, 1}};
    public static int[][] RH_PhisicBuff = {{264, 1}, {267, 1}, {268, 1}, {269, 1}, {271, 1}, {274, 1}, {275, 1}, {304, 1}, {310, 1}, {349, 1}, {364, 1}, {1035, 1}, {1036, 1}, {1259, 1}, {1307, 1}, {1352, 1}, {1357, 1}, {1364, 1}, {1388, 1}, {1397, 1}, {1460, 1}, {1461, 1}, {1501, 1}, {1502, 1}, {1504, 1}, {1517, 1}, {1519, 1}, {4699, 1}};
    public static boolean RH_Enable = false;
    public static boolean RH_FlagReturn = false;

    public static boolean TestAllAttack = false;
    public static boolean MultiHwidSystem = false;
    public static boolean OfflikePhysDamFormula = true;
    public static int FishingProtectChance = 0;
    public static int FishingProtectImageCount = 9;

    public static boolean VidakSystem = false;

    public static boolean ChatFilterEnable = false;
    public static int ChatFilterTextLength = 20;
    public static int ChatFilterTextTime = 5;
    public static int ChatFilterToUnban = 3600;
    public static int ChatFilterPercentStartsWith = 10;
    public static int ChatFilterPercentEndsWith = 10;
    public static int ChatFilterCountStartsWith = 10;
    public static int ChatFilterCountWith = 40;
    public static int ChatFilterTypeWith = 0;
    public static int ChatFilterTextCount = 3;
    public static int ChatFilterTextSizeClear = 50;

    public static boolean MailFilterEnable = false;
    public static int MailFilterTextLength = 20;
    public static int MailFilterTextTime = 5;
    public static int MailFilterPercentStartsWith = 10;
    public static int MailFilterPercentEndsWith = 10;
    public static int MailFilterCountStartsWith = 10;
    public static int MailFilterCountWith = 40;
    public static int MailFilterTypeWith = 0;
    public static int MailFilterTextCount = 3;
    public static int MailFilterTextSizeClear = 50;

    public static boolean ChatTellFilterEnable = false;
    public static int ChatTellFilterTextLength = 20;
    public static int ChatTellFilterTextTime = 5;
    public static int ChatTellFilterPercentStartsWith = 10;
    public static int ChatTellFilterPercentEndsWith = 10;
    public static int ChatTellFilterCountStartsWith = 10;
    public static int ChatTellFilterCountWith = 40;
    public static int ChatTellFilterTypeWith = 0;
    public static int ChatTellFilterTextCount = 3;
    public static int ChatTellFilterTextSizeClear = 50;

    public static boolean TalismanSumLife = false;
    public static int[] DellSkillIds = {};
    public static int[] DellSkillZoneIds = {};

    public static int OfflineBuffTitleColor = 255;
    public static int OfflineBuffNameColor = 11599871;

    public static int CertificationCount = 3;
    public static boolean AutolearnSimple = false;
    public static boolean NoelConfig1 = false;
    public static int NoelConfig2 = 9;

    public static int Attainment1_count = 500000;
    public static int Attainment2_count = 150;
    public static int Attainment3_count = 10;
    public static int Attainment4_count = 50;
    public static int Attainment6_count = 10;
    public static int Attainment7_count = 10;
    public static int Attainment8_count = 5;
    public static int Attainment11_count = 10;
    public static int Attainment12_count = 3;
    public static int Attainment13_count = 24;
    public static int Attainment14_count = 10;
    public static int Attainment10_radius = 1000;
    public static float Attainment14_damage = 3000f;
    public static int Attainment8_level = 80;
    public static int Attainment8_Time = 1440;
    public static int AttainmentKillProtectTime = 300;

    public static boolean EnableAttainment = false;
    public static int AttainmentType = -1;
    public static boolean AttainmentKillProtect = true;
    public static long[] Attainment1_reward = {};
    public static long[] Attainment2_reward = {};
    public static long[] Attainment3_reward = {};
    public static long[] Attainment4_reward = {};
    public static long[] Attainment5_reward = {};
    public static long[] Attainment6_reward = {};
    public static long[] Attainment7_reward = {};
    public static long[] Attainment8_reward = {};
    public static long[] Attainment9_reward = {};
    public static long[] Attainment10_reward = {};
    public static long[] Attainment11_reward = {};
    public static long[] Attainment12_reward = {};
    public static long[] Attainment13_reward = {};
    public static long[] Attainment14_reward = {};
    public static long[] Attainment15_reward = {};
    public static long[] Attainment16_reward = {};
    public static long[] Attainment17_reward = {};
    public static long[] Attainment18_reward = {};
    public static int Attainment9_DonateItem = 4037;
    public static float Attainment10_bonus = 1.2f;
    public static int[] AttainmentSkillReward = {};
    public static int[] AttainmentAnimation = {6791, 1, 2000};

    public static boolean EnableVidakReferal = false;
    public static long[] VidakReferalRewardToRefer = {57, 1000};
    public static long[] VidakReferalRewardToPlayer = {57, 1000};
    public static long[] VidakReferalRewardFor10Ref = {57, 1000};

    public static boolean WaterTest = false;
    public static long[] DoubleBaseClassPrice = {57, 1};
    public static int[] UnEquipItemList = {};

    public static boolean DebugRegion = false;
    public static boolean EnableBotReport = false;

    public static boolean PlayerRewardManager_Enable = false;
    public static int PlayerRewardManager_LevelForReward = 85;
    public static long[] PlayerRewardManager_LevelReward = {57L, 1L};

    public static int PlayerRewardManager_NobleCountReward = 1;
    public static long[] PlayerRewardManager_NobleReward = {57L, 1L};

    public static int PlayerRewardManager_PvpForReward = 10;
    public static int PlayerRewardManager_PvpCountReward = 1;
    public static long[] PlayerRewardManager_PvpReward = {57L, 1L};

    public static int PlayerRewardManager_PcForReward = 10;
    public static int PlayerRewardManager_PcCountReward = 1;
    public static long[] PlayerRewardManager_PcReward = {57L, 1L};

    public static boolean AffordShowBuff = false;
    public static boolean AffordShowTp = false;

    public static int VidakChatCharLife = -1;
    public static int ChatMessageInterval = 500;
    public static int ProtectHwidMask = 14;
    public static int TradeItemId = 57;

    public static boolean ShowAugmentInfo = false;
    public static boolean EnableLindvior = false;

    public static boolean L2VoteManagerEnabled = false;
    public static long L2VoteManagerInterval = 300000;
    public static String L2VoteSmsAddress = "";
    public static String L2VoteWebAddress = "";
    public static String L2VoteServerAddress = "open-team.ru";
    public static String L2VoteNamePrefix = "";
    public static int L2VoteSaveDays = 30;
    public static long[] L2VoteReward = {57, 20, 4037, 1};

    public static long BotReportAfkTime = 1440;
    public static int[] VisualSetList = {};
    public static int[] VisualNoSetList = {};
    public static int VisualPriceId = 57;
    public static long VisualPriceCount = 1000;

    public static int[] VisualWSetList = {40103, 40104, 40105, 40106, 40107, 40108, 40109, 40110, 40111, 40112, 40113, 40114, 40115, 40116, 40117, 40118, 40120, 40132, 40133, 40134, 40135, 40136};
    public static int[] VisualWNoSetList = {40103, 40104, 40105, 40106, 40107, 40108, 40109, 40110, 40111, 40112, 40113, 40114, 40115, 40116, 40117, 40118, 40120, 40132, 40133, 40134, 40135, 40136};
    public static int VisualWPriceId = 57;
    public static long VisualWPriceCount = 1000;

    public static int[] VisualSSetList = {40103, 40104, 40105, 40106, 40107, 40108, 40109, 40110, 40111, 40112, 40113, 40114, 40115, 40116, 40117, 40118, 40120, 40132, 40133, 40134, 40135, 40136};
    public static int[] VisualSNoSetList = {40103, 40104, 40105, 40106, 40107, 40108, 40109, 40110, 40111, 40112, 40113, 40114, 40115, 40116, 40117, 40118, 40120, 40132, 40133, 40134, 40135, 40136};
    public static int VisualSPriceId = 57;
    public static long VisualSPriceCount = 1000;
    public static int[] VisualSWearList = {};
    public static int VisualSWearTime = 10;

    public static int SkillTestVar = 0;
    public static boolean SellItemOneAdena = false;
    public static boolean CanAttackOlyNpc = false;

    public static boolean EnableTotalizator = false;
    public static boolean EnableOlyTotalizator = false;
    public static boolean EnableDebugOlyTotalizator = false;
    public static boolean TotalizatorEquipInfo = true;
    public static String TotalizatorAllowedItems = "57:10-200,4037:1-12";
    public static int[] TotalizatorNoTax = {4037};
    public static int TotalizatorMinItemNoTax = 10;
    public static int TotalizatorCabcelBidPercent = 10;
    public static int TotalizatorWinerBidPercent = 20;
    public static float TotalizatorWinerBidAdd = 0;
    public static boolean TotalizatorTeamWinerReward = false;

    public static boolean AcademicEnable = false;
    public static boolean RecruitmentAllow = false;
    public static int RecruitmentDescription = 10;
    public static int[] RecruitmentTime = {2, 3, 5, 6};
    public static int[] RecruitmentItems = {57, 4036, 4356};

    public static int[] BuffStoreNoSkill = {};
    public static boolean BuffStoreCheckCondSkill = false;
    public static boolean BuffStoreEnable = false;

    public static int Test1 = 100;
    public static int Test2 = 0;

    public static boolean RateMaxLoad = false;
    public static boolean L2HunterBonus = false;
    public static boolean EnableAutoAttribute = false;
    public static boolean AutoCpEnable = false;
    public static boolean CancelEbal = false;
    public static int AttackTest1 = 20; // 4
    public static int AttackTest2 = 0; // 1
    public static int ShowBuffType = -1; // 1

    public static long[] BotReportReward = {57, 100};
    public static boolean RequestSellItemCheckLastNpc = true;
    public static boolean CrazyZoneTpPk = false;
    public static int[] CrazyZoneTpPkLoc = {83400, 148040, -3431}; // 1
    public static int[] TeleportExcludeForbiddenZones = {0};
    public static String AdminDebugToLogin = "LOGIN.";
    public static int BoxDespawnTime31027 = 60000;
    public static int BoxDespawnTime31028 = 60000;
    public static int BoxDespawnTime31029 = 60000;
    public static int BoxDespawnTime31030 = 60000;
    public static boolean AutoCpOnlyPremim = false;
    public static boolean MysteriousAgentBuff = true;
    public static boolean VisualItemOlympiadDisable = false;
    public static boolean DisableRndDamage = false;

    public static int MinLevelToAddRaidPoint = 1;
    public static int MaxLevelToAddRaidPoint = 85;
    public static boolean _234_FatesWhisper = false;
    public static boolean CloakUseAllow = false;
    public static int[] CloakUseAllowList = {};

    public static long[] MatAddItemId = {};
    public static int ChatBanDay = 1;
    public static boolean EnableClassMasterWindowForLevelUp = false;
    public static int[] ClassMasterWindowForLevelUpJobList = {1, 2, 3, -1, -1};
    public static int AntharasMaxPlayer = 200;
    public static int ValakasMaxPlayer = 200;
    public static int FixintervalOfBeleth = 172800000; // 2 дня
    public static int RandomIntervalOfBeleth = 0;
    public static boolean LevelRateServiceEnable = false;
    public static float RateRbXp = 1f;
    public static float RateRbSp = 1f;
    public static float RateEpicXp = 1f;
    public static float RateEpicSp = 1f;
    public static boolean TradeOnlyUnblockZone = false;
    public static boolean TradeOnlyReflectionZone = false;
    public static boolean EnableMasterGift = false;
    public static long[] SecondClassReward = {};
    public static long[] ThirdClassReward = {};
    public static boolean EnableHtmlReward52 = false;
    public static long[] SearchTreasureRewardLooser = {57, 1000, 4037, 1};
    public static long[] SearchTreasureRewardWinner = {57, 1000000, 4037, 1000};
    public static int SeedofInfinityEkimusKill = 5;
    public static int SeedofDestructionOpenTime = 12;
    public static int SeedofDestructionOpenKillFirst = 10;
    public static int SeedofDestructionOpenKill = 10;
    public static boolean TutorialQuestEnable = true;
    public static int MonasteryOfSilenceGameReuse = 0;
    public static int EventBoxTime = 10;
    public static int[] EventBoxStartTime = {00, 00, 03, 00, 06, 00, 9, 00, 12, 00, 15, 00, 18, 00, 21, 00};
    public static boolean EventBoxHWID = false;
    public static boolean EventBoxOlympiadItems = false;
    public static int[] EventBoxForbiddenItems = {57};
    public static int[] EventBoxMinMaxLevel = {76, 85};

    public static boolean EventBoxCancel = false;
    public static int EventBoxEndTime = 300;
    public static long[] EventBoxRewardKill = {57, 20, 4037, 1};
    public static long[] EventBoxRewardKillNpc = {57, 20};
    public static long[] EventBoxRewardKillPlayer = {57, 20};
    public static long[] EventBoxRewardChest = {57, 20, 4037, 1};
    public static int EventBoxSpawnNpcId = 20001;
    public static int EventBoxSpawnNpcCount = 10;

    public static int EventBoxSpawnNpcResp = 30;
    public static int EventBoxWaitTime = 30;
    public static int EventBoxSpawnPlayer = 5;
    public static boolean EventBoxEnable = false;

    public static int[] ListObjectIdNoLogItemCount = {0};
    public static int[] ListItemIdNoLogItemCount = {57};
    public static int[] BlockItemList = {57};
    public static boolean BlockItemSetTeam = false;
    public static boolean BlockItemRemoveHeroSkill = false;
    public static int TestRevive = -1;

    public static boolean EnableFactionMod = false;
    public static boolean CounterAttackPvp = false;
    public static int[] CanByTradeItemsPA = {};

    public static boolean DebugMsg_THAT_IS_THE_INCORRECT_TARGET = false;
    public static boolean DebugMsg_INVALID_TARGET = false;
    public static boolean DebugMsg_CANNOT_SEE_TARGET = false;
    public static boolean DebugMsg_YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM = false;
    public static boolean DebugPckt_ServerClose = false;

    public static int BuffStoreItemId = 57;
    public static boolean EnablePvpTitleColor = false;
    public static int[] PvpTitleColorCount = {50};
    public static String[] PvpTitleColorList = {"0x00ff00"};

    public static boolean EnablePvpNameColor = false;
    public static int[] PvpNameColorCount = {50};
    public static String[] PvpNameColorList = {"0x00ff00"};

    public static boolean SendMsgToStore = false;
    public static boolean Enable2Pass = false;
    public static boolean Enable2PassAcc = false;
    public static boolean DragonVortexRBDrop2Summoner = false;
    public static boolean EnableClanPoint = false;
    public static int ClanPointTime1 = 120;
    public static int ClanPointTime2 = 60;
    public static int ClanPointTime3 = 360;
    public static int ClanPointCount1 = 0;
    public static int ClanPointCount2 = 0;
    public static int ClanPointCount3 = 0;
    public static int ClanPointCountExitClan = 0;
    public static int ClanPointCountEnterClan = 0;
    public static int[] ClanPointRaidId = {0};
    public static int[] ClanPointRaidCount = {0};
    public static int[] ClanPointRaidSiege = {0, 0};
    public static long[] Donate2Server = {57, 100};
    public static boolean NoInvitePartyForPvp = false;
    public static boolean NoInviteTradeForPvp = false;
    public static boolean NoInviteFriendForPvp = false;
    public static int WakeUpBaiumTime = 0;
    public static int EnableLogDamage = 0;
    public static int LogDamageLevelDiff = 0;
    public static int FactionModPointFactionToKillFriend = 0;
    public static int FactionModPointFactionToKillWar = 50;
    public static int FactionModPointPlayerToKillFriend = 0;
    public static int FactionModPointPlayerToKillWar = 50;
    public static int FactionModPointPlayerClassToKill = 15;
    public static boolean EnableNevitBonus = true;
    public static boolean SkillsShowChanceFull = false;
    public static boolean DebugChecktargetPlayable = false;

    public static int[] CBFameItem = {57};
    public static int[] CBFameItemPrice = {100};
    public static int[] CBFamePoint = {1};
    public static int[] FightClubForbiddenItems = {57};

    public static boolean FightClubOlympiadItems = false;

    public static int MaxKillerTime = 360;
    public static long[] MaxKillerReward1 = {57, 100000};
    public static long[] MaxKillerReward2 = {57, 10000};
    public static long[] MaxKillerReward3 = {57, 1000};
    public static long[] MaxKillerReward4 = {57, 100};
    public static long[] MaxKillerReward5 = {57, 10};
    public static boolean MaxKillerHwid = false;
    public static boolean MaxKillerKillTime = false;
    public static boolean ValakasDebug = false;

    public static boolean AllowFake = false;
    public static boolean FakeBlockMove = false;

    public static int MinSpawnDelay = 0;
    public static int MaxSpawnDelay = 1;
    public static int MinDespawnDelay = 350;
    public static int MaxDespawnDelay = 355;
    public static int FakeSpawnCollisionX = 50;
    public static int FakeSpawnCollisionY = 50;
    public static int DelayBeforeFirstWaveSpawn = 10;
    public static int FakeChanceToTalkSocial = 2;
    public static int FakeSocialChance = 3500;
    public static int FakeSitChance = 100;
    public static int DelayBeforeRefreshWave = 360;
    public static int FakeStepRange = 400;
    public static int FakeWalkChance = 3500;
    public static int[] FakeClasses = {88, 89, 90, 91, 92, 93, 94, 95, 99, 100, 101, 102, 103, 106, 107, 108, 109, 110, 113, 114, 115, 116, 117};
    public static int[] FakeNameColors = {};
    public static int[] FakeTitleColors = {};
    public static List<Integer> FAKE_PLAYERS_NON_SELLABLE_ITEMS = new ArrayList<Integer>();

    public static boolean NoTakeTerrFlagTransform = false;
    public static boolean NoTransformTerrFlag = false;

    /*************************** Ивент, защита долбаёба2 ***************************/
    public static boolean ProtectNpcEnable = false; // Включить ивент ?
    public static boolean ProtectNpcHWID = false;
    public static int[][] ProtectNpcMobTide = {{0}}; // Потоки мобов.
    public static int[][] ProtectNpcMobTideLoc = {{0, 0, 0}}; // Координаты спауна мобов...
    public static int[] ProtectNpcMobTideCount = {0}; // Количество мобов.
    public static int[] ProtectNpcMobTideHp = {0}; // Количество ХП у мобов с определенной волны.
    public static int[] ProtectNpcNpcDefenseLoc = {0, 0, 0}; // Координаты спауна НПСа которого нужно защитить.
    public static int[] ProtectNpcPlayerLoc = {0, 0, 0}; // Координаты спауна игроков.
    public static int[] ProtectNpcItemKillId = {0}; // Награда за убийство ID.
    public static int[] ProtectNpcItemId = {0}; // Награда ID.
    public static long[] ProtectNpcItemKillCount = {0}; // Награда за убийство количество.
    public static long[] ProtectNpcItemCount = {0}; // Награда количество.
    public static long[] ProtectNpcMobTideInterval = {0}; // Задержка спауна мобов в мс.
    public static int ProtectNpcNpcDefenseId = 0; // ID НПса которого нужно защитить.
    public static int ProtectNpcNpcDefenseHp = 0; // Количество ХП у НПса которого нужно защитить.
    public static int ProtectNpcEndTime = 10; // Время ивента. В минутах.
    public static int ProtectNpcStartTime = 10; // Время до старта ивента...
    public static int ProtectNpcTimeToBatle = 10; // Время до сапуна мобов...
    public static int ProtectNpcTimeInTide = 20; // Время между волнами
    public static int ProtectNpcPartyCount = 3; // Количество игроков в пати для частвия в ивенте.
    public static int ProtectNpcPlayerCount = 3; // Количество игроков в команде, для участвия в ивенте.
    public static String[] ProtectNpcMobTideAI = {"DefaultAI"}; // АИ мобов для каждой волны.
    public static int[] ProtectNpcStartTimeList = {-1};
    public static int[] ProtectNpcForbiddenItems = {57};
    public static boolean ProtectNpcOlympiadItems = false;
    public static int[] ProtectNpcItemRndId = {-1}; // Награда за убийство ID.
    public static long[] ProtectNpcItemRndCount = {-1}; // Награда за убийство ID.

    /*************************** Ивент, выроди тыкву ***************************/
    public static boolean EventSquashEnable = false;
    public static int EventSquashLifeTime = 43200; // Время жизни в минутаз, по умолчанию 30 дней.
    public static int EventSquashWatering = 1440; // Время полива в минутах, по умолчанию 24 часа.
    public static int EventSquashWateringCapture = 2880; // Время после последнего полива, когда можно захватить тыкву.
    public static int EventSquashWateringDied = 4320; // Время смерти после последнего полива.
    public static int[] EventSquashNextStage = {1440, 1440, 1440, 1440, 0}; // Время до поднятия следующего уровня.
    public static long[][] EventSquashItemRnd = {{57, 100}}; // Награда за убийство ID.
    public static int[] EventSquashNpc = {12774, 12775, 12777, 12778, 13017, 12776}; // Время до поднятия следующего уровня.
    public static int[] EventSquashRange = {10, 200};

    public static int AddRndItemForPvpChance = 0;
    public static int DebugBypassType = 0;
    public static boolean MutedIfEquipTerFlag = false;
    public static boolean EventBoxUseBuffer = false;
    public static boolean FortressSiege3h = false;
    public static boolean FakeOlyEnable = false;
    public static boolean FakeOlyForceEnable = false;

    public static int ChatTellCharLife = -1;
    public static int ChatShoutCharLife = -1;
    public static int ChatTradeCharLife = -1;
    public static int ChatAllCharLife = -1;

    public static int ChatTellCharOnline = -1;
    public static int ChatShoutCharOnline = -1;
    public static int ChatTradeCharOnline = -1;
    public static int ChatAllCharOnline = -1;

    public static boolean AllChatBan = false;
    public static boolean SpoilMinRate = false;
    public static boolean GeodataDebugMoveList = false;
    public static boolean GeodataDebugMoveList2 = false;
    public static boolean GeodataDebugNcanMoveNext = false;

    /*************************** CaptureTheFlag2.properties ***************************/
    public static int[] CaptureTheFlag2StartTime = {01, 00, 04, 00, 07, 00, 10, 00, 13, 00, 16, 00, 19, 00, 22, 00};
    public static int[] CaptureTheFlag2ForbiddenItems = {57};
    public static boolean CaptureTheFlag2IP = true;
    public static boolean CaptureTheFlag2HWID = false;
    public static int CaptureTheFlag2Time = 3;
    public static int CaptureTheFlag2EndTime = 600;
    public static boolean CaptureTheFlag2Cancel = false;
    public static boolean CaptureTheFlag2AddItemDraw = true;
    public static boolean CaptureTheFlag2Buff = false;
    public static int CaptureTheFlag2DanceAndSongTime = 300;
    public static int CaptureTheFlag2BuffTime = 300;
    public static boolean CaptureTheFlag2OlympiadItems = false;
    public static int[][] CaptureTheFlag2MagicBuff = {{264, 1}, {267, 1}, {268, 1}, {273, 1}, {276, 1}, {304, 1}, {349, 1}, {363, 1}, {365, 1}, {529, 1}, {530, 1}, {1035, 1}, {1078, 1}, {1085, 1}, {1259, 1}, {1303, 1}, {1307, 1}, {1352, 1}, {1364, 1}, {1389, 1}, {1397, 1}, {1413, 1}, {1461, 1}, {1500, 1}, {1501, 1}, {1503, 1}, {1504, 1}, {1517, 1}, {4703, 1}};
    public static int[][] CaptureTheFlag2PhisicBuff = {{264, 1}, {267, 1}, {268, 1}, {269, 1}, {271, 1}, {274, 1}, {275, 1}, {304, 1}, {310, 1}, {349, 1}, {364, 1}, {1035, 1}, {1036, 1}, {1259, 1}, {1307, 1}, {1352, 1}, {1357, 1}, {1364, 1}, {1388, 1}, {1397, 1}, {1460, 1}, {1461, 1}, {1501, 1}, {1502, 1}, {1504, 1}, {1517, 1}, {1519, 1}, {4699, 1}};
    public static boolean CaptureTheFlag2NoParty = false;
    public static boolean CaptureTheFlag2BattleUseBuffer = false;
    public static long[][] CaptureTheFlag2RewardWiner = {{57, 20}, {4037, 1}};
    public static long[] CaptureTheFlag2RewardKill = {57, 20, 4037, 1};
    public static int[] VisualWearList = {};
    public static int VisualWearTime = 10;
    public static int[] VisualWWearList = {};
    public static int VisualWWearTime = 10;
    public static int SellItemDiv = 2;
    public static int SellITaxPer = 0;
    public static boolean DropFixedQty = true;
    public static boolean OlympiadStatTarget = true;
    public static int ClanRequestWarTime = 0;

    public static int VidakSpawnRbId = 0;
    public static int[] VidakSpawnRbLoc = {};
    public static int VidakSpawnRbResp = 0;
    public static int VidakSpawnRbRespRnd = 0;
    public static int VidakSpawnNpcKillRbId = 0;
    public static int VidakSpawnNpcKillRbDespawn = 10;
    public static int VidakSpawnRbChanceReward = 0;
    public static int[] VidakSpawnRbReward = {57, 1000};

    public static int KLColorlvlClan = -1;
    public static String KLColor = "00FF00";

    public static boolean Premium2UseEnable = false;
    public static int[] Premium2UseItems = {4037};
    public static int[] Premium2UseDays = {1};
    public static float[] Premium2UseValue = {2f};

    public static int VidakSystemType = 0;

    public static int ZoneStartTime = -1;
    public static int ZoneEndTime = 0;
    public static int ZoneNextEnter = 60;
    public static boolean ZoneCheckHwid = false;
    public static long[] ZoneRewardFirstKill = {4037, 1};
    public static long[] ZoneRewardVictimKill = {4037, 100};
    public static long[] ZoneRewardVictimNoKill = {4037, 200};
    public static int ZoneNextTime = 30;
    public static boolean ZoneVictimEnable = false;
    public static int ZoneNextVictimTime = 20;
    public static int ZoneNextVictimTimeNoKill = 5;
    public static int ZoneVictimTime = 3;
    public static int ZoneVictimMinPlayer = 20;
    public static int ZoneMinLevel = 83;

    public static boolean DoubleDropAdenaForChamp = true;
    public static boolean D1 = false;
    public static boolean NoRateDrop4037 = false;
    public static boolean NoRateDrop10639 = false;
    public static boolean FixedRateDrop4037 = false;
    public static boolean DebugValidateLocation = false;
    public static boolean DebugValidateLocationTrace = false;
    public static boolean DebugOnAction = false;
    public static boolean DebugOnActionTrace = false;
    public static boolean DebugOnActionEvn = false;
    public static boolean DebugOnActionEvnTrace = false;
    public static boolean DebugOnActionDontMoveTrace = false;
    public static boolean DebugFollow = false;
    public static boolean DebugValidatePosition = false;
    public static boolean DebugCharMoveToLocationTrace = false;
    public static boolean DebugBroadcastMoveTrace = false;

    public static double PhysDamageMod = 1.;

    public static int NightmaresMinLevel = 20;
    public static int NightmaresMaxLevel = 80;

    public static boolean TerritorySiegeReturnFlags = false;
    public static boolean EnableCustomZoneToOutpost = false;
    public static boolean NoBuffDeadChar = false;

    public static boolean PokemonGoEnable = false;
    public static boolean PokemonGoHwid = false;

    public static boolean PokemonGoAnnounceToSpawn = true;
    public static int PokemonGoRespawnTime = 3600;
    public static long[] PokemonGoReward = {57, 1999, 4037, 100};
    public static int[] PokemonGoSpawnLoc = {0, 0, 0};

    public static long Warning4356Count = 10000;

    public static boolean AlwaysDropEpaulette = true;

    public static int Event123Time = 10;
    public static int[] Event123StartTime = {00, 00, 03, 00, 06, 00, 9, 00, 12, 00, 15, 00, 18, 00, 21, 00};
    public static boolean Event123HWID = false;
    public static boolean Event123OlympiadItems = false;
    public static int[] Event123ForbiddenItems = {57};
    public static boolean Event123Cancel = false;
    public static int Event123EndTime = 300;
    public static long[] Event123Reward = {57, 20, 4037, 1};
    public static long[] Event123RewardKillPlayer = {57, 20};
    public static int Event123WaitTime = 30;
    public static int Event123SpawnPlayer = 5;

    public static int LogOnlineDelay = 3600;

    public static int ItemEnchantDelay = 2000;
    public static int ItemEnchantDelay2 = 4000;
    public static boolean ai_reed_herb_enable = true;
    public static int Tournament_NoCarrier = 90;
    public static boolean EnableVisualPersonal = false;
    public static boolean EnableVisualEnchantPersonal = false;
    public static boolean DisableCloakPersonal = false;
    public static boolean EnableCancelFullResist = false;
    public static boolean EnableCancelRndCount = false;

    /**
     DELETE FROM droplist WHERE itemId='9912' AND mobId in ('35677','35680','35683','35713','35716','35719','35721','35746','35749','35752','35782','35785','35788','35790','35815','35818','35821','35846','35849','35852','35882','35885','35888','35890','35915','35918','35921','35951','35954','35957','35959','35989','35992','35995','35997','36022','36025','36028','36058','36061','36064','36066','36096','36099','36102','36104','36129','36132','36135','36160','36163','36166','36196','36199','36202','36204','36234','36237','36240','36242','36272','36275','36278','36280','36305','36308','36311','36341','36344','36347','36349','36379','36382','36385','36387', '35010','35011','35012','35013','35014','35015','35016','35017','35018','35019','35020','35021','35022','35023','35024','35025','35026','35027','35028','35029','35030','35031','35032','35033','35034','35035','35036','35037','35038','35039','35040','35041','35042','35043','35044','35045','35046','35047','35048','35049','35050','35051','35052','35053','35054','35055','35056','35057','35058','35059','35060','35061','35064','35065','35066','35067','35068','35069','35070','35071','35072','35073','35074','35075','35076','35077','35078','35079','35080','35081','35082','35083','35084','35085','35086','35087','35088','35089','35090','35091','35106','35107','35108','35109','35110','35111','35112','35113','35114','35115','35116','35117','35118','35119','35120','35121','35122','35123','35124','35125','35126','35127','35128','35129','35130','35131','35132','35133','35148','35149','35150','35151','35152','35153','35154','35155','35156','35157','35158','35159','35160','35161','35162','35163','35164','35165','35166','35167','35168','35169','35170','35171','35172','35173','35174','35175','35190','35191','35192','35193','35194','35195','35196','35197','35198','35199','35200','35201','35202','35203','35204','35205','35206','35207','35208','35209','35210','35211','35212','35213','35214','35215','35216','35217','35234','35235','35236','35237','35238','35239','35240','35241','35242','35243','35244','35245','35246','35247','35248','35249','35250','35251','35252','35253','35254','35255','35256','35257','35258','35259','35260','35280','35281','35282','35283','35284','35285','35286','35287','35288','35289','35290','35291','35292','35293','35294','35295','35296','35297','35298','35299','35300','35301','35302','35303','35304','35305','35306','35307','35325','35326','35327','35328','35329','35330','35331','35332','35333','35334','35335','35336','35337','35338','35339','35340','35341','35342','35343','35344','35345','35346','35347','35348','35349','35350','35351','35369','35370','35371','35372','35373','35374','35411','35412','35413','35414','35415','35416','35471','35472','35473','35474','35475','35476','35477','35478','35479','35480','35481','35482','35483','35484','35485','35486','35487','35488','35489','35490','35491','35492','35493','35494','35495','35496','35517','35518','35519','35520','35521','35522','35523','35524','35525','35526','35527','35528','35529','35530','35531','35532','35533','35534','35535','35536','35537','35538','35539','35540','35541','35542','35543','35632','35633','35634','35635','35636','35637','35670','35671','35672','35673','35674','35676','35678','35679','35681','35682','35684','35686','35687','35702','35703','35704','35705','35706','35711','35714','35715','35717','35718','35720','35722','35724','35725','35739','35740','35741','35742','35743','35745','35747','35748','35750','35751','35753','35755','35756','35771','35772','35773','35774','35775','35780','35783','35784','35786','35787','35789','35791','35793','35794','35808','35809','35810','35811','35812','35814','35816','35817','35819','35820','35822','35824','35825','35839','35840','35841','35842','35843','35845','35847','35848','35850','35851','35853','35855','35856','35871','35872','35873','35874','35875','35880','35883','35884','35886','35887','35889','35891','35893','35894','35908','35909','35910','35911','35912','35914','35916','35917','35919','35920','35922','35924','35925','35940','35941','35942','35943','35944','35949','35952','35953','35955','35956','35958','35960','35962','35963','35978','35979','35980','35981','35982','35987','35990','35991','35993','35994','35996','35998','36000','36001','36015','36016','36017','36018','36019','36021','36023','36024','36026','36027','36029','36031','36032','36047','36048','36049','36050','36051','36056','36059','36060','36062','36063','36065','36067','36069','36070','36085','36086','36087','36088','36089','36094','36097','36098','36100','36101','36103','36105','36107','36108','36122','36123','36124','36125','36126','36128','36130','36131','36133','36134','36136','36138','36139','36153','36154','36155','36156','36157','36159','36161','36162','36164','36165','36167','36169','36170','36185','36186','36187','36188','36189','36194','36197','36198','36200','36201','36203','36205','36207','36208','36223','36224','36225','36226','36227','36232','36233','36235','36236','36238','36239','36241','36243','36245','36246','36261','36262','36263','36264','36265','36270','36273','36274','36276','36277','36279','36281','36283','36284','36298','36299','36300','36301','36302','36304','36306','36307','36309','36310','36312','36314','36315','36330','36331','36332','36333','36334','36339','36342','36343','36345','36346','36348','36350','36352','36353','36368','36369','36370','36371','36372','36377','36380','36381','36383','36384','36386','36388','36390','36391','36508','36509','36510','36511','36512','36513','36514','36515','36516','36517','36518','36519','36520','36521','36522','36523','36524','36525','36526','36527','36528','36529','36530','36531','36532','36533','36534','36535','36536','36537','36538','36539','36540','36541','36542','36543','36544','36545','36546','36547','36548','36549','36550','36551','36552','36553','36554','36555','36556','36557','36558','36559','36560','36561');
     **/
    /**
     * INSERT INTO `droplist` VALUES ('35670', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('35671', '9912', '4', '16', '0', '781650', '1');
     * INSERT INTO `droplist` VALUES ('35672', '9912', '4', '12', '0', '718570', '1');
     * INSERT INTO `droplist` VALUES ('35673', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('35674', '9912', '4', '12', '0', '672110', '1');
     * INSERT INTO `droplist` VALUES ('35676', '9912', '4', '12', '0', '617370', '1');
     * INSERT INTO `droplist` VALUES ('35677', '9912', '140', '300', '0', '841370', '1');
     * INSERT INTO `droplist` VALUES ('35679', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('35680', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('35681', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('35682', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('35683', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('35684', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('35702', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('35703', '9912', '4', '16', '0', '780050', '1');
     * INSERT INTO `droplist` VALUES ('35704', '9912', '4', '12', '0', '719569', '1');
     * INSERT INTO `droplist` VALUES ('35705', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('35706', '9912', '4', '12', '0', '662110', '1');
     * INSERT INTO `droplist` VALUES ('35711', '9912', '24', '72', '0', '19311', '1');
     * INSERT INTO `droplist` VALUES ('35713', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('35715', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('35716', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('35717', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('35718', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('35719', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('35720', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('35721', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('35722', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('35739', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('35740', '9912', '4', '16', '0', '781650', '1');
     * INSERT INTO `droplist` VALUES ('35741', '9912', '4', '12', '0', '718570', '1');
     * INSERT INTO `droplist` VALUES ('35742', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('35743', '9912', '4', '12', '0', '672110', '1');
     * INSERT INTO `droplist` VALUES ('35745', '9912', '4', '12', '0', '617370', '1');
     * INSERT INTO `droplist` VALUES ('35746', '9912', '140', '300', '0', '841370', '1');
     * INSERT INTO `droplist` VALUES ('35748', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('35749', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('35750', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('35751', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('35752', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('35753', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('35771', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('35772', '9912', '4', '16', '0', '780050', '1');
     * INSERT INTO `droplist` VALUES ('35773', '9912', '4', '12', '0', '719569', '1');
     * INSERT INTO `droplist` VALUES ('35774', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('35775', '9912', '4', '12', '0', '662110', '1');
     * INSERT INTO `droplist` VALUES ('35780', '9912', '24', '72', '0', '19311', '1');
     * INSERT INTO `droplist` VALUES ('35782', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('35784', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('35785', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('35786', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('35787', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('35788', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('35789', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('35790', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('35791', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('35808', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('35809', '9912', '4', '16', '0', '781650', '1');
     * INSERT INTO `droplist` VALUES ('35810', '9912', '4', '12', '0', '718570', '1');
     * INSERT INTO `droplist` VALUES ('35811', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('35812', '9912', '4', '12', '0', '672110', '1');
     * INSERT INTO `droplist` VALUES ('35814', '9912', '4', '12', '0', '617370', '1');
     * INSERT INTO `droplist` VALUES ('35815', '9912', '140', '300', '0', '841370', '1');
     * INSERT INTO `droplist` VALUES ('35817', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('35818', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('35819', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('35820', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('35821', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('35822', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('35839', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('35840', '9912', '4', '16', '0', '781650', '1');
     * INSERT INTO `droplist` VALUES ('35841', '9912', '4', '12', '0', '718570', '1');
     * INSERT INTO `droplist` VALUES ('35842', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('35843', '9912', '4', '12', '0', '672110', '1');
     * INSERT INTO `droplist` VALUES ('35845', '9912', '4', '12', '0', '617370', '1');
     * INSERT INTO `droplist` VALUES ('35846', '9912', '140', '300', '0', '841370', '1');
     * INSERT INTO `droplist` VALUES ('35848', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('35849', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('35850', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('35851', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('35852', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('35853', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('35871', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('35872', '9912', '4', '16', '0', '780050', '1');
     * INSERT INTO `droplist` VALUES ('35873', '9912', '4', '12', '0', '719569', '1');
     * INSERT INTO `droplist` VALUES ('35874', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('35875', '9912', '4', '12', '0', '662110', '1');
     * INSERT INTO `droplist` VALUES ('35880', '9912', '24', '72', '0', '19311', '1');
     * INSERT INTO `droplist` VALUES ('35882', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('35884', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('35885', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('35886', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('35887', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('35888', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('35889', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('35890', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('35891', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('35908', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('35909', '9912', '4', '16', '0', '781650', '1');
     * INSERT INTO `droplist` VALUES ('35910', '9912', '4', '12', '0', '718570', '1');
     * INSERT INTO `droplist` VALUES ('35911', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('35912', '9912', '4', '12', '0', '672110', '1');
     * INSERT INTO `droplist` VALUES ('35914', '9912', '4', '12', '0', '617370', '1');
     * INSERT INTO `droplist` VALUES ('35915', '9912', '140', '300', '0', '841370', '1');
     * INSERT INTO `droplist` VALUES ('35917', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('35918', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('35919', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('35920', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('35921', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('35922', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('35940', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('35941', '9912', '4', '16', '0', '780050', '1');
     * INSERT INTO `droplist` VALUES ('35942', '9912', '4', '12', '0', '719569', '1');
     * INSERT INTO `droplist` VALUES ('35943', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('35944', '9912', '4', '12', '0', '662110', '1');
     * INSERT INTO `droplist` VALUES ('35949', '9912', '24', '72', '0', '19311', '1');
     * INSERT INTO `droplist` VALUES ('35951', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('35953', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('35954', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('35955', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('35956', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('35957', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('35958', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('35959', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('35960', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('35978', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('35979', '9912', '4', '16', '0', '780050', '1');
     * INSERT INTO `droplist` VALUES ('35980', '9912', '4', '12', '0', '719569', '1');
     * INSERT INTO `droplist` VALUES ('35981', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('35982', '9912', '4', '12', '0', '662110', '1');
     * INSERT INTO `droplist` VALUES ('35987', '9912', '24', '72', '0', '19311', '1');
     * INSERT INTO `droplist` VALUES ('35989', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('35991', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('35992', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('35993', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('35994', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('35995', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('35996', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('35997', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('35998', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('36015', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('36016', '9912', '4', '16', '0', '781650', '1');
     * INSERT INTO `droplist` VALUES ('36017', '9912', '4', '12', '0', '718570', '1');
     * INSERT INTO `droplist` VALUES ('36018', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('36019', '9912', '4', '12', '0', '672110', '1');
     * INSERT INTO `droplist` VALUES ('36021', '9912', '4', '12', '0', '617370', '1');
     * INSERT INTO `droplist` VALUES ('36022', '9912', '140', '300', '0', '841370', '1');
     * INSERT INTO `droplist` VALUES ('36024', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('36025', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('36026', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('36027', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('36028', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('36029', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('36047', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('36048', '9912', '4', '16', '0', '780050', '1');
     * INSERT INTO `droplist` VALUES ('36049', '9912', '4', '12', '0', '719569', '1');
     * INSERT INTO `droplist` VALUES ('36050', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('36051', '9912', '4', '12', '0', '662110', '1');
     * INSERT INTO `droplist` VALUES ('36056', '9912', '24', '72', '0', '19311', '1');
     * INSERT INTO `droplist` VALUES ('36058', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('36060', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('36061', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('36062', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('36063', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('36064', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('36065', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('36066', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('36067', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('36085', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('36086', '9912', '4', '16', '0', '780050', '1');
     * INSERT INTO `droplist` VALUES ('36087', '9912', '4', '12', '0', '719569', '1');
     * INSERT INTO `droplist` VALUES ('36088', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('36089', '9912', '4', '12', '0', '662110', '1');
     * INSERT INTO `droplist` VALUES ('36094', '9912', '24', '72', '0', '19311', '1');
     * INSERT INTO `droplist` VALUES ('36096', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('36098', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('36099', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('36100', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('36101', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('36102', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('36103', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('36104', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('36105', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('36122', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('36123', '9912', '4', '16', '0', '781650', '1');
     * INSERT INTO `droplist` VALUES ('36124', '9912', '4', '12', '0', '718570', '1');
     * INSERT INTO `droplist` VALUES ('36125', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('36126', '9912', '4', '12', '0', '672110', '1');
     * INSERT INTO `droplist` VALUES ('36128', '9912', '4', '12', '0', '617370', '1');
     * INSERT INTO `droplist` VALUES ('36129', '9912', '140', '300', '0', '841370', '1');
     * INSERT INTO `droplist` VALUES ('36131', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('36132', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('36133', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('36134', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('36135', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('36136', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('36153', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('36154', '9912', '4', '16', '0', '781650', '1');
     * INSERT INTO `droplist` VALUES ('36155', '9912', '4', '12', '0', '718570', '1');
     * INSERT INTO `droplist` VALUES ('36156', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('36157', '9912', '4', '12', '0', '672110', '1');
     * INSERT INTO `droplist` VALUES ('36159', '9912', '4', '12', '0', '617370', '1');
     * INSERT INTO `droplist` VALUES ('36160', '9912', '140', '300', '0', '841370', '1');
     * INSERT INTO `droplist` VALUES ('36162', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('36163', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('36164', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('36165', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('36166', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('36167', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('36185', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('36186', '9912', '4', '16', '0', '780050', '1');
     * INSERT INTO `droplist` VALUES ('36187', '9912', '4', '12', '0', '719569', '1');
     * INSERT INTO `droplist` VALUES ('36188', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('36189', '9912', '4', '12', '0', '662110', '1');
     * INSERT INTO `droplist` VALUES ('36194', '9912', '24', '72', '0', '19311', '1');
     * INSERT INTO `droplist` VALUES ('36196', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('36198', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('36199', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('36200', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('36201', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('36202', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('36203', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('36204', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('36205', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('36223', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('36224', '9912', '4', '16', '0', '780050', '1');
     * INSERT INTO `droplist` VALUES ('36225', '9912', '4', '12', '0', '719569', '1');
     * INSERT INTO `droplist` VALUES ('36226', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('36227', '9912', '4', '12', '0', '662110', '1');
     * INSERT INTO `droplist` VALUES ('36232', '9912', '24', '72', '0', '19311', '1');
     * INSERT INTO `droplist` VALUES ('36233', '9912', '4', '6', '0', '460000', '1');
     * INSERT INTO `droplist` VALUES ('36234', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('36236', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('36237', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('36238', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('36239', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('36240', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('36241', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('36242', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('36243', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('36261', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('36262', '9912', '4', '16', '0', '780050', '1');
     * INSERT INTO `droplist` VALUES ('36263', '9912', '4', '12', '0', '719569', '1');
     * INSERT INTO `droplist` VALUES ('36264', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('36265', '9912', '4', '12', '0', '662110', '1');
     * INSERT INTO `droplist` VALUES ('36270', '9912', '24', '72', '0', '19311', '1');
     * INSERT INTO `droplist` VALUES ('36272', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('36274', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('36275', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('36276', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('36277', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('36278', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('36279', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('36280', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('36281', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('36298', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('36299', '9912', '4', '16', '0', '781650', '1');
     * INSERT INTO `droplist` VALUES ('36300', '9912', '4', '12', '0', '718570', '1');
     * INSERT INTO `droplist` VALUES ('36301', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('36302', '9912', '4', '12', '0', '672110', '1');
     * INSERT INTO `droplist` VALUES ('36304', '9912', '4', '12', '0', '617370', '1');
     * INSERT INTO `droplist` VALUES ('36305', '9912', '140', '300', '0', '841370', '1');
     * INSERT INTO `droplist` VALUES ('36307', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('36308', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('36309', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('36310', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('36311', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('36312', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('36330', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('36331', '9912', '4', '16', '0', '780050', '1');
     * INSERT INTO `droplist` VALUES ('36332', '9912', '4', '12', '0', '719569', '1');
     * INSERT INTO `droplist` VALUES ('36333', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('36334', '9912', '4', '12', '0', '662110', '1');
     * INSERT INTO `droplist` VALUES ('36339', '9912', '24', '72', '0', '19311', '1');
     * INSERT INTO `droplist` VALUES ('36341', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('36343', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('36344', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('36345', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('36346', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('36347', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('36348', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('36349', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('36350', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('36368', '9912', '40', '200', '0', '900000', '2');
     * INSERT INTO `droplist` VALUES ('36369', '9912', '4', '16', '0', '780050', '1');
     * INSERT INTO `droplist` VALUES ('36370', '9912', '4', '12', '0', '719569', '1');
     * INSERT INTO `droplist` VALUES ('36371', '9912', '4', '12', '0', '662800', '1');
     * INSERT INTO `droplist` VALUES ('36372', '9912', '4', '12', '0', '662110', '1');
     * INSERT INTO `droplist` VALUES ('36377', '9912', '24', '72', '0', '19311', '1');
     * INSERT INTO `droplist` VALUES ('36379', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('36381', '9912', '4', '16', '0', '730100', '1');
     * INSERT INTO `droplist` VALUES ('36382', '9912', '140', '300', '0', '841571', '1');
     * INSERT INTO `droplist` VALUES ('36383', '9912', '4', '16', '0', '775740', '1');
     * INSERT INTO `droplist` VALUES ('36384', '9912', '4', '16', '0', '823820', '1');
     * INSERT INTO `droplist` VALUES ('36385', '9912', '140', '300', '0', '842235', '1');
     * INSERT INTO `droplist` VALUES ('36386', '9912', '4', '16', '0', '710000', '1');
     * INSERT INTO `droplist` VALUES ('36387', '9912', '40', '154', '0', '704766', '1');
     * INSERT INTO `droplist` VALUES ('36388', '9912', '40', '154', '0', '704766', '1');
     **/
    public static boolean DropEpauleteOnlyReg = false;

    public static int[] Odyssey_zone_id = {};
    public static int[] Odyssey_loc = {12184, 16888, -4584};

    public static long[] Odyssey_party_item = {58004, 1};
    public static int[] Odyssey_party_loc = {153576, 142088, -12736};

    public static long[][] Odyssey_player_item = {{57, 1}, {57, 1}, {57, 1}};
    public static int[] Odyssey_player_loc = {-28744, 75016, -2904};

    public static boolean Odyssey_zone_sp_enable = false;
    public static int Odyssey_zone_sp_delay = 60000;
    public static int Odyssey_zone_sp_zone_id = 1;
    public static int Odyssey_zone_sp_level = 1;
    public static int[] Odyssey_zone_sp_tele_loc = {0, 0, 0};
    public static int[] Odyssey_zone_sp_return_loc = {203940, -111840, 66};
    public static int[][] Odyssey_zone_sp_reward = {{}};

    public static boolean Odyssey_zone_exp_enable = false;
    public static int Odyssey_zone_exp_delay = 60000;
    public static int Odyssey_zone_exp_zone_id = 1;
    public static int Odyssey_zone_exp_level = 1;
    public static int[] Odyssey_zone_exp_tele_loc = {0, 0, 0};
    public static int[] Odyssey_zone_exp_return_loc = {203940, -111840, 66};
    public static int[][] Odyssey_zone_exp_reward = {{}};

    public static int[] BuyPointsItemId = {};
    public static int BuyPointsForOneItem = 10;

    public static int ChaosFestivalTime = 10; // Время до начала ивента...Время в минутах...
    public static int ChaosFestivalRegTime = 300; // Время для регистрации...Время в секундах...
    public static int ChaosFestivalRegFoulTime = 420; // Время применения штрафа на отмену реги...Время в секундах...
    public static int ChaosFestivalWaitTime = 60; // Время на подготовку после телепортации...
    public static int[][] ChaosFestivalMagicBuffList = {{1323, 1}};
    public static int[][] ChaosFestivalPhisicBuffList = {{1323, 1}};
    public static int ChaosFestivalDanceAndSongTime = 300;
    public static int ChaosFestivalBuffTime = 300;
    public static int[][] ChaosFestivalStartTime = {{-1}};
    public static boolean ChaosFestivalHWID = false;
    public static boolean ChaosFestivalOlympiadItems = true;
    public static int[] ChaosFestivalForbiddenItems = {6406, 6407, 5234, 5235, 5236, 5237, 8664, 8665, 8666, 5238, 5239, 5240, 5241, 8667, 8668, 8669, 8670, 5242, 5243, 5244, 5245, 5246, 5247, 5248, 8671, 8672, 8673, 8674, 8675, 8676, 8677};
    public static boolean ChaosFestivalCancel = true;
    public static boolean ChaosFestivalRoot = true;
    public static int ChaosFestivalEndTime = 300;
    public static long[] ChaosFestivalRewardPeriodWinner = {57, 20, 4037, 1};
    public static long[] ChaosFestivalReward = {57, 20, 4037, 1};
    public static long[] ChaosFestivalRewardKillPlayer = {57, 20};
    public static long[] ChaosFestivalAddItem = {57, 1};


    public static boolean p_reflect_dd_use_pet = false;
    public static boolean ReNameAnnouncements = false;
    public static boolean AntQueenUnTransform = false;
    public static boolean BaiumUnTransform = false;
    public static boolean AntharasUnTransform = false;
    public static boolean ValakasUnTransform = false;

    public static boolean EnableVisualItemStat = true;

    public static int ReturnTerritoryFlag = 0;

    // ---------------------------------------------------------------------------
    /**
     * Размер буфера на чтение
     */
    public static int READ_BUFFER_SIZE = 65536;
    /**
     * Размер буффра на запись
     */
    public static int WRITE_BUFFER_SIZE = 131072;
    /**
     * Максимальное количество пакетов при проходе на запись, может быть меньше этого числа, если буфер записи будет заполнен
     */
    public static int MAX_SEND_PER_PASS = 32;
    /**
     * Задержка в миллимекундах после каждого прохода в цикле SelectorThread
     */
    public static long SLEEP_TIME = 3;
    /**
     * Задержка перед сменой запланированного интересуемого действия
     */
    public static long INTEREST_DELAY = 3;
    /**
     * Размер заголовка
     */
    public static int HEADER_SIZE = 2;
    /**
     * Максимальный размер пакета
     */
    public static int PACKET_SIZE = 32768;
    /**
     * Количество вспомогательных буферов
     */
    public static int HELPER_BUFFER_COUNT = 64;
    /**
     * Таймаут ожидания авторизации клиента в миллимекундах
     */
    public static long AUTH_TIMEOUT = 30000L;
    /**
     * Таймаут ожидания закрытия коннекта в миллимекундах
     */
    public static long CLOSEWAIT_TIMEOUT = 10000L;
    /**
     * Размер очереди соединений
     */
    public static int BACKLOG = 1024;
    /**
     * Порядок байтов
     */
    public static ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    public static int PacketStatsType = 0;

    // ----------
    public static int ExpSpPenaltyDiff = 100;
    public static int DropPenaltyDiff = 100;

    public static int[] RouletteItemTake = {57, 100};
    public static long[][] RouletteItemList = {{57, 1}, {4037, 1}, {6656, 1}, {6657, 1}, {6658, 1}, {6659, 1}, {6660, 1}, {6661, 1}, {6662, 1}, {14465, 1}, {14466, 1}, {14467, 1}, {14468, 1}, {14469, 1}, {14164, 1}, {16025, 1}, {16027, 1}, {7837, 1}, {7838, 1}, {7839, 1}, {7840, 1}};

    /**
     * 0 - Передвигается ячейка. Статический порядок предметов.
     * 1 - Передвигается ячейка. Рандомный порядок предметов, возможны повторы.
     * 2 - Статическая ячейка, крутится список итемов. Статический порядок предметов.
     * 3 - Статическая ячейка, крутится список итемов. Рандомный порядок предметов, возможны повторы.
     **/
    public static int RouletteType = 0;
    public static int RouletteChanceNextItem = 10;
    public static int[] RouletteNextItemList = {4037};
    public static int[] RouletteRnd = {6, 7};
    public static int[] RouletteAnnounceItemList = {};
    public static boolean RouletteEnableAnnounce = true;

    public static boolean EnableFameProtect = true;
    public static boolean SummonSiegeZone = false;

    public static int[] BattleZoneForbiddenItems = {57};
    public static long[] BattleZoneRewardKill = {57, 1000};
    public static long[][] BattleZoneRewardClan = {{57, 1000}, {57, 100}};
    public static long[][] BattleZoneRewardPlayer = {{57, 1000}, {57, 100}};
    public static int BattleZoneTimeToBattle = 5;
    public static int BattleZoneTimeBattle = 30;

    public static boolean Tournament_DellClanSkill = false;
    public static boolean Tournament_CanRessurect = false;
    public static long[] Tournament_RegItem = {};

    public static boolean Attainment13_ResetStepOnRelog = false;
    public static boolean Attainment13_ResetTimeOnRelog = false;
    public static boolean Attainment13_EnableCheckHwid = true;
    public static boolean Attainment13_EnableCycleReward = true;

    public static int Attainment13_Level = 40;
    public static int Attainment13_Chance = -1;
    public static int Attainment13_Minute = 1;
    public static int Attainment13_HwidCount = 100000;
    public static int Attainment13_Font = 3;// Small,  Normal,  Large,  VeryLarge,  Giant
    public static int Attainment13_ScreenPos = 0;// 1024-TopRightRelative,  2-TopRight,  0-TopLeft,  1-TopCenter,  6-MiddleRight,  4-MiddleLeft,  5-MiddleCenter,  10-BottomRight,  8-BottomLeft,  9-BottomCenter
    public static int Attainment13_FontStyle = 1;// 0-Normal,  1-Shadowed
    public static int[] Attainment13_Color = {0xFF, 0xFF, 0xFF, 0xFF};
    public static short[] Attainment13_xy = {0, 0};
    public static long[][] Attainment13_Set = {};

    public static int EnchantDecriseTimer = 60;
    public static int EnchantDecriseLevelArmor = 1000000;
    public static int EnchantDecriseLevelWeapon = 1000000;

    public static long[] RouletNpcItem = {57, 1};
    public static long[][] RouletNpcItemRnd = {{57, 1, 90}, {57, 10, 90}};

    public static long RelogTime = 60000;

    public static float PersonalAdenaRate = 1f;

    public static boolean MmoTopStartWithServer2 = false;
    public static boolean MmoTopShowConsoleInfo2 = true;
    public static long MmoTopManagerInterval2 = 300000;
    public static String MmoTopWebAddress2 = "";
    public static long[] MmoTopReward2 = {57, 20, 4037, 1};
    public static long[] MmoTopRewardRnd2 = {};

    public static boolean NoRaitDropRb = false;
    public static boolean TullyWorkshopZoneEnable = true;

    public static int NoblessSellSubLevel = 75;
    public static int JobLevel3 = 76;
    public static int MaxSpawnLevel = 9999;
    public static int[] AddExpSpUseItems = {0};
    public static long[][] AddExpSpUseValue = {{0, 0}};
    public static boolean NaiaLockCanFail = true;
    public static boolean AlwaysStartingAdena = false;
    public static boolean GiveNobleForThirdProfession = false;

    public static long CharacterCreateSP = 0;
    public static boolean CannotCreateKamael = false;

    public static int Certification65Level = 65;
    public static int Certification70Level = 70;
    public static int Certification75Level = 75;
    public static int Certification80Level = 80;

    public static boolean CanNotByTradeItem = false;
    public static int[] CanNotByTradeItems = {};
    public static boolean CharacterCreateNoble = false;
    public static int[] CharacterCreateLoc = {};

    public static int RequestRefineCancel_C1 = 95000;
    public static int RequestRefineCancel_C2 = 150000;
    public static int RequestRefineCancel_C3 = 210000;

    public static int RequestRefineCancel_B1 = 240000;
    public static int RequestRefineCancel_B2 = 270000;

    public static int RequestRefineCancel_A1 = 330000;
    public static int RequestRefineCancel_A2 = 390000;
    public static int RequestRefineCancel_A3 = 480000;

    public static int RequestRefineCancel_S1 = 480000;
    public static int RequestRefineCancel_S2 = 920000;
    public static int RequestRefineCancel_S3 = 1400000;
    public static int RequestRefineCancel_S4 = 2800000;
    public static int RequestRefineCancel_S5 = 3600000;
    public static int RequestRefineCancel_S6 = 3200000;

    public static int RequestRefineCancelOld_s = 3200000;
    public static int RequestRefineCancelOldGrade = 3200000;

    public static int OlympiadFirstGruopCount = 50;

    /**
     * * * * * * выполняемая команда
     * - - - - -
     * | | | | |
     * | | | | ----- День недели (0 - 7) (Воскресенье =0 или =7)
     * | | | ------- Месяц (1 - 12)
     * | | --------- День (1 - 31)
     * | ----------- Час (0 - 23)
     * ------------- Минута (0 - 59)
     **/
    public static String AntharasCronResp = "* * * * *";
    public static String BaiumCronResp = "* * * * *";
    public static String ValakasCronResp = "* * * * *";

    public static boolean EnableAutoEnchant = false;
    public static boolean EnableAutoEnchantOnlyPa = false;

    public static int HennaItemId = 57;
    public static int HennaRemoveItemId = 57;
    public static int HennaRemoveItemCount = -1;

    public static int CertificationRemovePrice = 10000000;

    // Минимальная видимость(с учетом Z) клиента 2040, максимальная 4040.
    public static int BroadcastAttackRadius = 1500;
    public static int BroadcastAttackHeight = 300;

    public static int[] ThirdClassListToReward = {};
    public static int[] ThirdClassListToRewardId = {};
    public static long[] ThirdClassListToRewardCount = {};

    public static int MaxPetLevel = 86;
    public static int CancelEbalMin = 2;

    public static boolean OlympiadDebug1 = false;

    public static int ToiRespawnTime = 7200000;
    public static String ToiDespawnTime = "00 19 * * *";

    public static boolean FreyaCloseDoor = false;
    public static int OlyStatMinBattle = 10;

    public static int[] DeathMatchForbiddenItems = {57};
    public static long[] DeathMatchRewardKill = {57, 1000};
    public static long[][] DeathMatchRewardPlayer = {{57, 1000}, {57, 100}};
    public static int DeathMatchTimeToBattle = 60; // время на подготовку
    public static int DeathMatchTimeBattle = 30; // время боя
    public static int DeathMatchRegTime = 5; // время боя
    public static int DeathMatchResurrectTime = 3; // время реса
    public static boolean DeathMatchUseBuffer = false;
    public static boolean EnableShuffleSkill = false;
    public static int[] UnJailLocation = {};

    public static int OnActionShiftAggroLimit = 50;

    public static boolean EnableItemEnchantLog = true;
    public static boolean DisableSkillOnJail = false;

    public static boolean DEbug1 = false;
    public static boolean DEbug2 = false;
    public static boolean DEbug3 = false;
    public static boolean DEbug4 = false;
    public static boolean DEbug5 = false;
    public static boolean DEbug6 = false;
    public static boolean DEbug7 = false;
    public static boolean DEbug8 = false;
    public static boolean DEbug9 = false;
    public static boolean DEbug10 = false;
    public static boolean DebugMoveIsPlayer = false;

    public static int DebugMove1 = 16;
    public static float DebugMove2 = 72f;

    public static int GeoMoveFindPathType = 1;
    public static int MoveToLineType = 0;

    public static int MoveTickIntervalType = 1;

    /**
     * 0 - поиск пути работает всегда.
     * 1 - поиск пути работает только на передвижение/следование, на атаку не работает.
     * 2 - поиск пути работает только на передвижение, на атаку и следование не работает.
     **/
    public static int FollowFindPathType = 0;
    public static int MoveZCorrect = 0;
    public static boolean DebugMoveStackIsPlayer = false;
    public static boolean AttackInBarrierSummon = true;
    public static boolean AttackInBarrierPlayer = true;
    public static boolean AttackInBarrierNpc = true;

    public static int RBInstanceSpawn_EndTimme = 70;
    public static int RBInstanceSpawn_RbId = 37017;
    public static int[][] RBInstanceSpawn_MobList = {{37015, 50, 30}, {37016, 20, 30}};

    /**
     * * * * * * выполняемая команда
     * - - - - -
     * | | | | |
     * | | | | ----- День недели (0 - 7) (Воскресенье =0 или =7)
     * | | | ------- Месяц (1 - 12)
     * | | --------- День (1 - 31)
     * | ----------- Час (0 - 23)
     * ------------- Минута (0 - 59)
     **/
    public static String RBInstanceSpawn_CronResp = "35 18,21 * * *";

    public static boolean FreyaHwidProtect = false;
    public static boolean FrintezzaHwidProtect = false;
    public static boolean ZakenHwidProtect = false;
    public static boolean CabrioHwidProtect = false;
    public static boolean LabaHwidProtect = false;
    public static boolean UnsummonSiegePetInEpicZone = false;

    public static boolean BlockTargetMacros = false;
    public static int[] OlympiadStadiums = {1, 2, 3, 4};

    public static int NORMAL_ENCHANT_COST_MULTIPLIER = 1;
    public static int SAFE_ENCHANT_COST_MULTIPLIER = 5;

    public static boolean LoginProxyEnable = false;

    public static int SetMaxTaxSealNone = 15;
    public static int SetMaxTaxSealDusk = 5;
    public static int SetMaxTaxSealDawn = 25;

    public static long[][] AddRndItemForPvp = {}; // Награда за убийство ID.
    public static long[][] OlyWeanTeamAddRndItemForPvp = {}; // Награда за убийство ID.

    public static boolean CharacterSetSkillNoble = false;
    public static boolean MinEnchantForOlympiadEnable = false;
    public static int MinEnchantWeaponForOlympiad = -1;
    public static int MinEnchantArmorForOlympiad = -1;
    public static int MinEnchantJewelForOlympiad = -1;
    public static int SetFameForPvpPk = -1;
    public static int[] ListItemToPartyDistribute = {};

    public static long[] ChestReward = {};
    public static int[] ChestMobSpawnList = {};
    public static int ChestDespawnTime = 30;
    public static int ChestSpawnChance = 5; // 10000 == 100%

    public static int[] NPCMobSpawnList = {};
    public static long[] NPCMobSpawnKillReward = {};
    public static int NPCDespawnTime = 30;
    public static int NPCSpawnChance = 5; // 10000 == 100%
    public static int NPCSpawnId = -1;

    public static boolean FreyaNeedCommandChanel = true;
    public static boolean FrintezzaNeedCommandChanel = true;
    public static boolean TiataNeedCommandChanel = true;
    public static boolean EnableRbBlockOverDamage = true;

    /************************************** HeadHunter **************************************/
    public static boolean HeadHunter_Debug = false;
    public static String HeadHunter_AllowedItems = "57,4037";

    public static int HeadHunter_TaskLifeTime = 259200;
    public static int HeadHunter_TaxReg = 5;
    public static int HeadHunter_TaxAnonim = 20;
    public static long HeadHunter_TaxAnnons = 10;
    public static int HeadHunter_TaxIdAnnons = 57;

    public static boolean EnableEmerlPet = false;
    public static boolean DropV2 = false;

    public static boolean SetNoobleForKillBarakiel = false;

    public static boolean AllowAddCastleRewards = false;

    public static long[][] GludioCastleRewards = {};
    public static long[][] DionCastleRewards = {};
    public static long[][] GiranCastleRewards = {};
    public static long[][] OrenCastleRewards = {};
    public static long[][] AdenCastleRewards = {};
    public static long[][] InnadrilCastleRewards = {};
    public static long[][] GoddardCastleRewards = {};
    public static long[][] RuneCastleRewards = {};
    public static long[][] ShuttgartCastleRewards = {};

    public static long[][] GludioLeadCastleRewards = {};
    public static long[][] DionLeadCastleRewards = {};
    public static long[][] GiranLeadCastleRewards = {};
    public static long[][] OrenLeadCastleRewards = {};
    public static long[][] AdenLeadCastleRewards = {};
    public static long[][] InnadrilLeadCastleRewards = {};
    public static long[][] GoddardLeadCastleRewards = {};
    public static long[][] RuneLeadCastleRewards = {};
    public static long[][] ShuttgartLeadCastleRewards = {};

    public static boolean EnableRewardForMonsters = false;
    public static long[][] RewardForMonsters = {};

    public static int AnnounceSpawnNpcType = -1;
    public static int[] AnnounceSpawnNpcList = {};

    public static long[] ZoneDayReward = {};
    public static long[] ZoneWeekReward1st = {};
    public static long[] ZoneWeekReward2st = {};
    public static long[] ZoneWeekReward3st = {};

    public static boolean OlympiadStatEnable = false;
    public static long[] OlympiadDayReward = {};
    public static long[] OlympiadWeekReward1st = {};
    public static long[] OlympiadWeekReward2st = {};
    public static long[] OlympiadWeekReward3st = {};

    public static int[] AutoCpPointsHp = {1539, 1540};
    public static int[] AutoCpPointsMp = {728};
    public static int[] AutoCpPointsCp = {5592, 5591};

    public static float DropChanceMod = 1f;
    public static float SpoilChanceMod = 1f;

    public static double MaxWeaponTraitMod = 2d;
    public static double MinWeaponTraitMod = 0.05d;

    public static int MinCancellationChance = 25;
    public static int MaxCancellationChance = 75;

    public static int MinBaneChance = 40;
    public static int MaxBaneChance = 90;

    public static boolean DebugSkillAdd1 = false;
    public static boolean DebugSkillAdd2 = false;

    /************************************** TheHungerGames **************************************/
    /**
     * * * * * * выполняемая команда
     * - - - - -
     * | | | | |
     * | | | | ----- День недели (0 - 7) (Воскресенье =0 или =7)
     * | | | ------- Месяц (1 - 12)
     * | | --------- День (1 - 31)
     * | ----------- Час (0 - 23)
     * ------------- Минута (0 - 59)
     **/
    public static String TheHungerGames_Start = "00 15 * * 2,3,4,5,6,7";
    public static String TheHungerGames_StartTurnir = "00 18 * * 1";

    public static boolean TheHungerGames_NeedNoble = true;
    public static boolean TheHungerGames_HWID = false;
    public static boolean TheHungerGames_BlockArmor = false;
    public static boolean TheHungerGames_BlockAccessory = false;
    public static boolean TheHungerGames_Cancel = true;
    public static boolean TheHungerGames_Root = true;
    public static boolean TheHungerGames_BlockChat = true;
    public static boolean TheHungerGames_ClearName = true;
    public static boolean TheHungerGames_ClearTitle = true;
    public static boolean TheHungerGames_ClearCrest = true;
    public static boolean TheHungerGames_ClearCrestAlly = true;
    public static boolean TheHungerGames_DellClanSkill = true;
    public static boolean TheHungerGames_DellSquadSkill = true;
    public static boolean TheHungerGames_DellHeroSkill = true;
    public static boolean TheHungerGames_ClearNameCB = true;
    public static boolean TheHungerGames_AutoEquipWeapon = true;
    public static boolean TheHungerGames_ObservOnlySponsor = true;
    public static boolean TheHungerGames_CbInfo = false;

    public static int TheHungerGames_StartRegCount = 25;
    public static int TheHungerGames_MinPlayerInArena = 13;
    public static int TheHungerGames_MaxPlayerInArena = 25;
    public static int TheHungerGames_WaitTime = 60;
    public static int TheHungerGames_DanceAndSongTime = 300;
    public static int TheHungerGames_BuffTime = 300;
    public static int TheHungerGames_AddPoint = 1;
    public static int TheHungerGames_MaxItemCount = 2;
    public static int TheHungerGames_BatlleTime = 600;
    public static int TheHungerGames_PlayerCountToSpawnNpc = 5;
    public static int TheHungerGames_PauseToSpawnNpc = 180;
    public static int TheHungerGames_VisualChestSlot = -1;
    public static int TheHungerGames_StartMsgType = 0;
    public static int TheHungerGames_TurnirStartTime = 600;
    public static int TheHungerGames_ClanRep = 0;
    public static int TheHungerGames_TurnirClanRep = 0;
    public static int TheHungerGames_EndTime = 300;
    public static int TheHungerGames_SponsorStart = 60;

    public static float TheHungerGames_TurSponsorPriceMod = 3f;

    public static long TheHungerGames_TrapTime = 60;

    public static long[] TheHungerGames_AddItem = {57, 1};
    public static long[] TheHungerGames_RewardKillPlayer = {57, 1};
    public static long[] TheHungerGames_Reward = {57, 1};
    public static long[] TheHungerGames_TurnirReward = {57, 1};

    public static long[][] TheHungerGames_RndRewardSponsorOnce = {};
    public static long[][] TheHungerGames_RndRewardSponsorOnceTur = {};
    public static long[][] TheHungerGames_RndRewardSponsorWinner = {};
    public static long[][] TheHungerGames_RndRewardSponsorWinnerTur = {};

    public static int[] TheHungerGames_ItemNoBlock = {1467, 3952, 1345};
    public static int[] TheHungerGames_SpawnNpcLoc = {213000, 181208, -256};
    public static int[] TheHungerGames_EventSponsor_Price = new int[0];
    public static int[] TheHungerGames_EventSponsor_AddToken = new int[0];
    public static int[] TheHungerGames_NotUseSkill = new int[0];
    public static int[] TheHungerGames_UnBlockChatList = {};

    public static int[][] TheHungerGames_TeleportLoaction =
            {
                    {214618, 179866, -262}, // right
                    {214604, 179653, -262},
                    {214562, 179445, -261},
                    {214495, 179245, -262},
                    {214401, 179054, -262},
                    {214281, 178876, -262},
                    {214143, 178716, -262},
                    {213983, 178575, -262},
                    {213805, 178459, -262},
                    {213615, 178364, -262},
                    {213414, 178296, -262},
                    {213206, 178253, -262},
                    {212993, 178240, -262}, // center
                    {212780, 178252, -261},
                    {212570, 178295, -262},
                    {212370, 178363, -262},
                    {212179, 178457, -262},
                    {212002, 178575, -261},
                    {211842, 178716, -262},
                    {211702, 178875, -262},
                    {211583, 179053, -261},
                    {211489, 179242, -262},
                    {211422, 179445, -262},
                    {211379, 179653, -262},
                    {211365, 179865, -262}
            };

    public static int[][] TheHungerGames_ClassGroup =
            {

            };

    public static int[][] TheHungerGames_ItemsSpawn =
            {
                    // id, count, group, x, y, z, rnd_min, rnd_max
                    {15567, 1, -1, 213144, 179816, -256, 0, 0},
                    {15567, 1, -1, 213144, 179768, -256, 0, 0},
                    {15567, 1, -1, 213128, 179720, -256, 0, 0},
                    {15567, 1, -1, 213112, 179672, -256, 0, 0},
                    {15567, 1, -1, 213080, 179640, -256, 0, 0},
                    {15567, 1, -1, 213032, 179592, -256, 0, 0},
                    {15567, 1, -1, 212984, 179576, -256, 0, 0},
                    {15567, 1, -1, 212920, 179576, -256, 0, 0},
                    {15567, 1, -1, 212872, 179592, -256, 0, 0},
                    {15567, 1, -1, 212840, 179624, -256, 0, 0},
                    {15567, 1, -1, 212824, 179688, -256, 0, 0},
                    {15567, 1, -1, 212824, 179768, -256, 0, 0},
                    {15567, 1, -1, 212824, 179832, -256, 0, 0},
                    {15567, 1, -1, 212824, 179736, -256, 0, 0}
            };

    public static int[][][] TheHungerGames_ItemsTaskSpawn = {};

    public static int[][] TheHungerGames_MagicBuffList = {{1323, 1}};
    public static int[][] TheHungerGames_PhisicBuffList = {{1323, 1}};

    public static int DG1 = 8;
    public static int DG2 = 8;
    public static boolean DG3 = true;

    public static boolean FreyaseHardSkill = true;

    /************************************** Tournament1 **************************************/
    public static int[] Tournament1_StartTime = {01, 00, 04, 00, 07, 00, 10, 00, 13, 00, 16, 00, 19, 00, 22, 00};
    public static int[] Tournament1_ForbiddenItems = {57};

    public static int[][] Tournament1_MagicBuffList = {{1323, 1}};
    public static int[][] Tournament1_PhisicBuffList = {{1323, 1}};

    public static long[] Tournament1_Reward = {57, 1};
    public static long[] Tournament1_RewardKillPlayer = {57, 1};

    public static boolean Tournament1_NeedNoble = false;
    public static boolean Tournament1_HWID = false;
    public static boolean Tournament1_Root = true;
    public static boolean Tournament1_DellClanSkill = true;
    public static boolean Tournament1_DellSquadSkill = true;
    public static boolean Tournament1_DellHeroSkill = true;
    public static boolean Tournament1_Cancel = true;
    public static boolean Tournament1_OlympiadItems = false;
    public static boolean Tournament1_UseBuffer = false;

    public static int Tournament1_BatlleTime = 600;
    public static int Tournament1_RegTime = 600;
    public static int Tournament1_DanceAndSongTime = 300;
    public static int Tournament1_BuffTime = 300;
    public static int Tournament1_WaitTime = 30;

    /************************************** Tournament2 **************************************/
    public static int[] Tournament2_StartTime = {01, 00, 04, 00, 07, 00, 10, 00, 13, 00, 16, 00, 19, 00, 22, 00};
    public static int[] Tournament2_ForbiddenItems = {57};
    public static int[] Tournament2_PlayerTeleportToRbLoc = {16328, 213144, -9352};
    public static int[] Tournament2_RaidSpawnLoc = {20001, 16328, 213144, -9352};

    public static int[][] Tournament2_MagicBuffList = {{1323, 1}};
    public static int[][] Tournament2_PhisicBuffList = {{1323, 1}};

    public static long[] Tournament2_Reward = {57, 1};
    public static long[] Tournament2_RewardKillPlayer = {57, 1};

    public static boolean Tournament2_NeedNoble = false;
    public static boolean Tournament2_HWID = false;
    public static boolean Tournament2_Root = true;
    public static boolean Tournament2_DellClanSkill = true;
    public static boolean Tournament2_DellSquadSkill = true;
    public static boolean Tournament2_DellHeroSkill = true;
    public static boolean Tournament2_Cancel = true;
    public static boolean Tournament2_OlympiadItems = false;
    public static boolean Tournament2_UseBuffer = false;

    public static int Tournament2_BatlleTime = 600;
    public static int Tournament2_RegTime = 600;
    public static int Tournament2_DanceAndSongTime = 300;
    public static int Tournament2_BuffTime = 300;
    public static int Tournament2_WaitTime = 30;

    public static int ClanPlayerLimit0 = 10;
    public static int ClanPlayerLimit1 = 15;
    public static int ClanPlayerLimit2 = 20;
    public static int ClanPlayerLimit3 = 30;
    public static int ClanPlayerLimit = 40;
    public static int ClanPlayerLimitAcR1R2 = 20;
    public static int ClanPlayerLimitAcR1R2_11 = 30;
    public static int ClanPlayerLimitAc = 20;
    public static int ClanPlayerLimitAc11 = 30;
    public static int ClanPlayerLimitK1K2 = 10;
    public static int ClanPlayerLimitK1K2_9 = 25;
    public static int ClanPlayerLimitK3K4 = 10;
    public static int ClanPlayerLimitK3K4_10 = 25;

    public static boolean EnableNevitAbnormal = true;

    public static double[] ActivateHeroReward = {};
    public static double[] OlyWinRewardItems = {};
    public static double[] OlyLoserRewardItems = {};

    public static int OlyWinnerRewardClanRep = 0;
    public static int OlyLoserRewardClanRep = 0;

    public static int OlyWinnerRewardFame = 0;
    public static int OlyLoserRewardFame = 0;

    public static int ModSmsCharCount = 1;
    public static long[] ModSmsAddItem = {};

    public static int[] AutoLootSpecialList = {57, 4037, 9627};
    public static int[] NoRateDropList = {4037, 10639};
    public static int[] FixedRateDropList = {};
    public static boolean Attainment13_Msg = true;
    public static boolean Attainment13_Msg2 = false;
    public static int[] PremiumBuffer_Price = {4037, 10639};

    public static boolean BotNoName = false;
    public static int BotIsChargedSpiritShot = 0;

    public static int AddChanceToCraftMasterworkPa = 0;
    public static int AddChanceToCraftPa = 0;

    public static int[] ZoneToBlockTransform = {};

    public static boolean DisableDbClean = false;

    public static boolean EnableModDrop = false;

    public static boolean CaptureTheFlag_RewardFirstKiller = false;

    public static long[] CaptureTheFlag_RewardKillPlayer = {57, 20, 100};
    public static long[] TeamvsTeam_RewardKillPlayer = {57, 20, 100};
    public static long[] LastHero_RewardKillPlayer = {57, 20, 100};

    public static long[] CaptureTheFlag_RewardWinner = {4037, 20, 100};
    public static long[] TeamvsTeam_RewardWinner = {4037, 20, 100};
    public static long[] LastHero_RewardWinner = {4037, 20, 100};

    public static int[] BbsOnlineMsg = {100, 500, 1000, 2000};
    public static String[] BbsOnlineColor = {"000000", "000000", "000000", "000000"};

    public static boolean EnableDamageOnScreen = false;
    public static boolean EnableDamageOnScreenOld = false;
    public static boolean DamageOnScreenOldShowNotSkillIcon = false;
    public static int DamageOnScreenFontId = 3;
    public static int DamageOnScreenRPosX = 250;
    public static int DamageOnScreenRPosY = 250;
    public static int DamageOnScreenHPosX = 250;
    public static int DamageOnScreenHPosY = 250;
    public static int DamageOnScreenRSizeX = 16;
    public static int DamageOnScreenRSizeY = 16;
    public static int DamageOnScreenHSizeX = 16;
    public static int DamageOnScreenHSizeY = 16;
    public static int DamageOnScreenColorRSkillName = 16711680;
    public static int DamageOnScreenColorRDmgMsg = 16711680;
    public static int DamageOnScreenColorHSkillName = 16777215;
    public static int DamageOnScreenColorHDmgMsg = 16777215;

    public static boolean p_target_me_broken = false;

    public static int ClanWarMaxCount = 30;
    public static int ClanWarMinMember = 15;
    public static int ClanWarMinLevel = 3;

    public static int MarcketLotLifeTime = 172800;
    public static int MarcketPriceId = 4357;
    public static int MarcketMaxLot = 10;
    public static int MarcketPriceMin = 1;
    public static int MarcketPriceMax = 1000000;
    public static int MarcketExchangeMin = 100;
    public static int MarcketExchangeMax = 1000000;
    public static int MarcketExchangeItemId = 4037;
    public static int[] MarcketPriceToLot = {};
    public static float MarcketExchangeRate = 10;
    public static int[] MarcketAddPriceList = {};
    public static int[] MarcketOtherItemList = {};
    public static int[] MarcketAddItemList = {};
    public static int[] MarcketNotItemList = {};

    public static boolean StatisticRaidShowMin = true;
    public static boolean NeedQuestsSoiHallOfErosionDefence = true;
    public static boolean NeedQuestsSoiHallOfSuffering = true;
    public static int DelayedItemsLogClan = -1;

    public static int CabrioCofferDespawn = 120;
    public static int[] Icrease78LevelLoc = {};

    public static int ValakasWaitingToSpawn = 120;
    public static int ValakasWaitingToSleep = 7200;

    public static boolean SendToConcolePacketLog = true;

    public static boolean allowPhantoms = false;
    public static int firstWaveDelay = 1;
    public static int waveRespawn = 25;
    public static int[] phantomSpawnDelayMinMax = {0, 1};
    public static int[] phantomDespawnDelayMinMax = {25, 30};
    public static boolean everybodyMaxLevel = false;
    public static int minEnchant = 6;
    public static int maxEnchant = 16;
    public static double enchantChance = 50;
    public static long townAiTick = 5763;
    public static long townAiInit = 10426;
    public static long chatAnswerDelay = 5738;
    public static double chatAnswerChance = 15;
    public static int randomMoveDistance = 1500;
    public static double randomMoveChance = 1;
    public static int[] userActions = {0};
    public static double userActionChance = 0.3;
    public static int moveToNpcRange = 1500;
    public static double moveToNpcChance = 1;

    public static boolean AttainmentHwidProtect = true;

    public static int[] AttainmentIn_PvE = {1000, 2000, 5000, 7000, 10000, 14000, 18000, 22000, 26000, 30000};
    public static int[][] AttainmentIn_PvE_Reward = {{7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}};

    public static int[] AttainmentIn_PvP = {100, 200, 400, 600, 900, 1200, 1600, 2000, 2500, 3000};
    public static int[][] AttainmentIn_PvP_Reward = {{7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}};

    public static int[] AttainmentIn_Pk = {50, 100, 200, 300, 450, 600, 800, 1000, 1250, 1500};
    public static int[][] AttainmentIn_Pk_Reward = {{7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}};

    public static int[] AttainmentIn_Time = {24, 48, 96, 144, 243, 288, 384, 480, 600, 720};
    public static long[][] AttainmentIn_Time_Reward = {{57, 1}, {57, 1}, {57, 1}, {57, 1}, {57, 1}, {57, 1}, {57, 1}, {57, 1}, {57, 1}, {57, 1}};

    public static int[] AttainmentIn_RbKill = {5, 10, 15, 20, 25, 30, 40, 50, 60, 70};
    public static int[][] AttainmentIn_RbKill_Reward = {{7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}};

    public static int[] AttainmentIn_Instance = {10, 20, 40, 60, 80, 100, 130, 160, 200, 250};
    public static long[][] AttainmentIn_Instance_Reward = {{57, 1}, {57, 1}, {57, 1}, {57, 1}, {57, 1}, {57, 1}, {57, 1}, {57, 1}, {57, 1}, {57, 1}};

    public static int[] AttainmentIn_EnchantWeapon = {30, 90, 120, 150, 180, 210, 240, 270, 300, 330};
    public static int[][] AttainmentIn_EnchantWeapon_Reward = {{7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}};

    public static int[] AttainmentIn_EnchantArmor = {30, 90, 120, 150, 180, 210, 240, 270, 300, 330};
    public static int[][] AttainmentIn_EnchantArmor_Reward = {{7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}};

    public static int[] AttainmentIn_Quest = {30, 80, 130, 200, 300};
    public static int[][] AttainmentIn_Quest_Reward = {{7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}, {7029, 1}};
    public static boolean AttainmentIn_QuestOnlyDisposable = true;

    public static int[] AttainmentIn_Craft = {50, 200, 500};
    public static int[][] AttainmentIn_Craft_Reward = {{7029, 1}, {7029, 1}, {7029, 1}};

    public static int[] AddVisualItemList = {};

    public static int RequestRecipeShopManageQuitPacketDelay = 1500;
    public static int RequestBypassToServerPacketDelay = 100;
    public static int AppearingPacketDelay = 500;
    public static int RequestPrivateStoreQuitBuyPacketDelay = 1500;
    public static int RequestReloadPacketDelay = 1000;
    public static int RequestActionUsePacketDelay = 100;
    public static int RequestExBR_LectureMarkPacketDelay = 1500;
    public static int RequestPartyMatchListPacketDelay = 1500;
    public static int RequestPreviewItemPacketDelay = 1500;
    public static int RequestPrivateStoreQuitSellPacketDelay = 1500;
    public static int RequestRecipeShopListSetPacketDelay = 1500;
    public static int RequestRefineCancelPacketDelay = 1500;
    public static int RequestWithdrawalPledgePacketDelay = 1500;
    public static int SetPrivateStoreBuyListPacketDelay = 1500;
    public static int SetPrivateStoreListPacketDelay = 1500;
    public static int RequestMagicSkillUsePacketDelay = 100;
    public static int RequestSetPledgeCrestPacketDelay = 10000;
    public static int RequestSetPledgeCrestLargePacketDelay = 10000;
    public static int SendWareHouseDepositListPacketDelay = 1000;
    public static int SendWareHouseWithDrawListPacketDelay = 1000;
    public static int EnterWorldPacketDelay = 100;

    /************************************** DotaEvent **************************************/
    /**
     * * * * * * выполняемая команда
     * - - - - -
     * | | | | |
     * | | | | ----- День недели (0 - 7) (Воскресенье =0 или =7)
     * | | | ------- Месяц (1 - 12)
     * | | --------- День (1 - 31)
     * | ----------- Час (0 - 23)
     * ------------- Минута (0 - 59)
     **/
    public static String DotaEvent_Start = "00 15 * * 2,3,4,5,6,7";

    public static boolean DotaEvent_EventCycle = false;
    public static boolean DotaEvent_NoUseTransformation = true;
    public static boolean DotaEvent_NeedNoble = true;
    public static boolean DotaEvent_HWID = false;
    public static boolean DotaEvent_Cancel = true;
    public static boolean DotaEvent_Root = true;
    public static boolean DotaEvent_DellClanSkill = true;
    public static boolean DotaEvent_DellSquadSkill = true;
    public static boolean DotaEvent_DellHeroSkill = true;
    public static boolean DotaEvent_OlympiadItems = false;
    public static boolean DotaEvent_UseBuffer = false;

    public static int DotaEvent_EventCycleTime = 60;
    public static int DotaEvent_StartRegCount = 25;
    public static int DotaEvent_MinPlayerInArena = 13;
    public static int DotaEvent_MaxPlayerInArena = 25;
    public static int DotaEvent_WaitTime = 60;
    public static int DotaEvent_DanceAndSongTime = 300;
    public static int DotaEvent_BuffTime = 300;
    public static int DotaEvent_BatlleTime = 600;
    public static int DotaEvent_StartMsgType = 0;
    public static int DotaEvent_EndTime = 300;
    public static int DotaEvent_MobAttackCountMin = 5;
    public static int DotaEvent_MobAttackCountMax = 10;
    public static int DotaEvent_MobAttackResp = 30;

    public static int[] DotaEvent_RespaunTime = {5};
    public static int[] DotaEvent_ForbiddenItems = {57};

    public static long[] DotaEvent_RewardKillPlayer = {57, 1};
    public static long[] DotaEvent_RewardKillMob = {57, 1};
    public static long[] DotaEvent_RewardAllMobKillAltar = {57, 1};
    public static long[] DotaEvent_RewardAllPlayerKillAltar = {57, 1};
    public static long[] DotaEvent_RewardPlayerKillAltar = {57, 1};

    public static int[][] DotaEvent_MagicBuffList = {{1323, 1}};
    public static int[][] DotaEvent_PhisicBuffList = {{1323, 1}};

    public static int MinRespawnTime = 20;
    public static int GoldLakfiChance = 20;

    public static boolean Dev_ZoneLazyTaskUse = false;
    public static long Dev_LazyTaskPurgeTime = 60000L;
    public static long Dev_AITaskPurgeTime = 60000L;
    public static long Dev_AttTaskPurgeTime = 600000L;

    public static float CastInterruptMod = 1;
    public static int CastInterrupt = 10;
    public static int CastInterruptCrit = 75;

    public static String BypassManagerSimple = "^(_mrsl|_clbbs|_mm|_diary|friendlist|friendmail|manor_menu_select|_match|_olympiad).*";
    public static String BypassManagerSimpleBbs = "^(_bbshome|_bbsgetfav|_bbslink|_bbsloc|_bbsclan|_bbsmemo|_maillist_0_1_0_|_friendlist_0_|_bbs_call_cfg|_bbs_call_acp|_bbs_call_anim|_bbs_call_relog|_bbs_call_ofline|_bbs_call_tal|_bbs_call_help|_bbs_call_pa).*";

    public static boolean BanAtFlood = false;
    public static boolean ChangePasswordAtFlood = false;
    public static boolean EnableSpawnBloodAltarNpc = true;
    public static boolean EnableBloodAltarManager = true;

    public static boolean MailOnEnterGame = false;
    public static String MailOnEnterGameSenderName = "Admin";
    public static String MailOnEnterGameTopic = "Admin";
    public static String MailOnEnterGameBody = "Admin";

    public static int[][] MailOnDonateItem = {};
    public static String MailOnDonateSenderName = "Admin";
    public static String MailOnDonateTopic = "Admin";
    public static String MailOnDonateBody = "Admin";
    public static long[][] MailOnDonateAddItem = {};

    public static int ItemProtectErr = 3;
    public static int ItemProtectBanTime = 300;

    public static boolean BloackHealRb = false;
    public static boolean ShiftItemToTradeChats = false;
    public static boolean EnableTraceClient = false;
    public static boolean EnableBugNpcStat = false;
    public static boolean BugNpcStatLevelMod = false;

    public static int LoginServerProtocol = 2;
    public static int[][] RaidToRandomDrop = {};
    public static long[][] RaidToRandomDropList = {};
    public static int[] EnableEnchantCloak = {};

    public static boolean UseAltLethal1 = false;
    public static boolean UseAltLethal2 = false;
    public static double UseAltLethal2NpcPer = 30;
    public static double UseAltLethal2PlayerPer = 20;

    public static boolean BonusCodeCheckCharId = false;
    public static boolean BonusCodeCheckCharHwid = false;
    public static int BonusCodeErrCount = 5;
    public static int BonusCodeBlockTime = 5;
    public static int BonusCodeBlockTimeAdd = 5;
    public static int WakeUpBaiumTimer = 0;

    public static boolean SetApperWall = false;
    public static int VitalityMax = 10000;
    public static boolean PremiumHeroSetSkill = true;
    public static int AttainmentOlympiadWin = 5;
    public static int AttainmentOlympiadLoose = 5;
    public static long[] AttainmentOlympiadWin_reward = {};
    public static long[] AttainmentOlympiadLoose_reward = {};

    public static boolean LoginServerProtectEnable = false;
    public static long LoginServerConnectCount = 3;
    public static long LoginServerConnectBanTime = 600;
    public static long LoginServerTryCheckDuration = 1000;

    public static boolean EnableLogPacketSize = false;
    public static boolean GameServerProtectEnable = false;
    public static boolean GameServerProtectEnablePacketCheck = false;
    public static long GameServerPacketCount = 100;
    public static long GameServerPacketTryCheckDuration = 10;
    public static long GameServerPPSTryCheckDuration = 1000;
    public static long GameServerPPSCount = 100;

    public static boolean GameServerProtectAcceptEnable = false;
    public static boolean GameServerProtectBanErrPacketSize = false;
    public static long GameServerConnectCount = 3;
    public static long GameServerConnectBanTime = 600;
    public static long GameServerTryCheckDuration = 1000;
    public static long GameServerPacketBanTime = 600;

    public static boolean VisualSetZeroEnchant = false;
    public static boolean CanByTradePvpItem = false;
    public static int PartyPenaltyMinDiff = 10;
    public static int PartyPenaltyMaxDiff = 14;

    public static boolean Strix_Antibrute = false;
    public static boolean Strix_Ban = true;
    public static boolean Strix_AllowText = false;
    public static String Strix_Text = "l2j-dev.ru";


    public static boolean CustomOlyEnable = false;
    public static boolean CustomOlyForceEnable = false;
    /**
     * * * * * * выполняемая команда
     * - - - - -
     * | | | | |
     * | | | | ----- День недели (0 - 7) (Воскресенье =0 или =7)
     * | | | ------- Месяц (1 - 12)
     * | | --------- День (1 - 31)
     * | ----------- Час (0 - 23)
     * ------------- Минута (0 - 59)
     **/
    public static String CustomOlyCron = "45 22 * * *";
    public static int CustomOlyEndTime = 75;
    public static double[] CustomOlyWinRewardItems = {};
    public static double[] CustomOlyLoserRewardItems = {};

    public static boolean NotShowCrestOnTrade = false;
    public static int AltItemAuctionAnnounce = -1;

    public static String LastHeroStartTimeCron = "00 18,20 * * *";
    public static String CaptureTheFlagStartTimeCron = "00 18,20 * * *";
    public static String TeamvsTeamStartTimeCron = "00 18,20 * * *";
    public static String RH_StartTimeCron = "00 18,20 * * *";
    public static String EventBoxStartTimeCron = "00 18,20 * * *";
    public static String Tournament_StartTournCron = "00 18,20 * * *";
    public static int[] Tournament_StartTournRegTime = {120, 5};

    public static boolean DubugTraceCloseConnection = false;
    public static int StartPremiumType = 0;

    public static int[] HuntinFishing_TimeToStart = {00, 00};
    public static String HuntinFishing_TimeToStartCron = "00 18,20 * * *";
    public static long[] HuntinFishing_RevardKill = {57, 100, 100};
    public static int[] HuntinFishing_MobLevelRnd = {80, 85};
    public static int[] HuntinFishing_CharacterCountRnd = {5, 10};
    public static int[] HuntinFishing_MonsterCountKillRnd = {5, 10};
    public static int HuntinFishing_MonsterCountInServer = 50;
    public static int HuntinFishing_TimeToStartBefore = 600;
    public static int[] HuntinFishing_NotSelectedMob = {};


    public static boolean TakeDrop_Enable = false;
    public static int[] TakeDrop_TimeToStart = {00, 00};
    public static String TakeDrop_TimeToStartCron = "00 18,20 * * *";
    public static long[] TakeDrop_Reward = {57, 100, 100};
    public static long[][] TakeDrop_ItemToDrop = {{57, 10, 1000}};
    public static int[] TakeDrop_CharacterCountRnd = {5, 10};
    public static int TakeDrop_TimeToStartBefore = 600;

    public static boolean TakeFish_Enable = false;
    public static int[] TakeFish_TimeToStart = {00, 00};
    public static String TakeFish_TimeToStartCron = "00 18,20 * * *";
    public static long[] TakeFish_Reward = {57, 100, 100};
    public static long[][] TakeFish_FishList = {{6411, 1, 5}};
    public static int[] TakeFish_CharacterCountRnd = {5, 10};
    public static int TakeFish_TimeToStartBefore = 600;

    public static int[] AttainmentIn_Helper = {};
    public static int[][] AttainmentIn_HelperLoc = {};
    public static boolean RecallToRequest = false;
    public static boolean NpcRealDistance3D = true;
    public static int FriendInviteMinLevelPlayer = 1;
    public static int FriendInviteMinLevelTarget = 1;
    public static int BuffStoreRadius = 30;
    public static boolean BuffStoreOnlyFar = false;
    public static boolean BuffStoreOnlyPice = false;
    public static int BuffStoreMinLevel = 1;
    public static int BuffStorePriceItem = 57;
    public static int BuffStorePrice = 0;
    public static boolean AllowOfflineBuffStore = false;

    public static boolean RequestMultiSellChooseAugmentSet = true;
    public static boolean TestMoveInHide = false;

    public static String FantomeDefaultPath = "data/bot/";
    public static boolean BotSystemEnable = false;
    public static boolean BotSystemWriteAI = false;
    public static boolean FantomeWriteGmAI = false;
    public static boolean BotSystemTownZone = false;
    public static boolean BotSystemEnableLog = false;
    public static boolean BotSystemEnableSayAI = false;
    public static boolean BotSystemEnableSayTask = false;
    public static boolean BotSystemRejectAI_Attack = true;
    public static long StartAiDelayMin = 10;
    public static long StartAiDelayMax = 60;
    public static long MaxActionDelay = 3000000;
    public static long DespawnDelayMin = 10;
    public static long DespawnDelayMax = 60;
    public static long SpawnDelayMin = 10;
    public static long SpawnDelayMax = 60;
    public static long BotSystemMinLifeTime = 180;
    public static long FirstWaveDelay = 1;
    public static long BotSystemSpawnDelay = 5;
    public static long LastAiActive = 1800;
    public static double BotSystemPerBot = 1.;
    public static int BotSystemBotCount = 0;
    public static int BotSystemMaxItemEnchant = 7;
    public static long TimeDespawnBehindTown = 30;
    public static int BotSystemSpawnMinLevel = 1;
    public static int BotSystemSpawnMaxLevel = 85;
    public static int BotSystemSayAllRndChance = 0;
    public static int BotSystemSayShoutRndChance = 0;
    public static int BotSystemSayTradeRndChance = 0;
    public static int[] BotSystemEnableSayTaskTime = {120, 10000};
    /*
	# если чар кого-то атаковал(физ атака или юз офенсив скила) или его кто-то атаковал
	# 0 - будет то что ты видишь...
	# 1 - аи бракуется и не записывается
	# 2 - аи сохраняет в момент атаки
	BotSystemRejectType = 0
	*/
    public static int BotSystemRejectType = 0;

    public static boolean CharacterCreate350q = false;
    public static boolean CharacterEnter350q = false;

    public static boolean GeoEngineA1 = false;
    public static boolean GeoEngineA2 = false;
    public static boolean OldTimeToReuseAttack = false;
    public static boolean MaxInstancesPremium = false;
    public static int MaxInstances = 1000000;
    public static float CraftMasterworkBodyMod = 1;
    /*************************** Zombi.properties ***************************/
    public static boolean Zombi_OlympiadItems = false;
    public static String Zombi_StartTimeCron = "00 18,20 * * *";
    public static int[] Zombi_StartTime = {01, 00, 04, 00, 07, 00, 10, 00, 13, 00, 16, 00, 19, 00, 22, 00};
    public static int[] Zombi_RewardKillZombi = {57, 1000, 100};
    public static int[] Zombi_RewardKillPlayer = {57, 1000, 100};
    public static int[] Zombi_RewardWinner = {57, 1000, 100};
    public static int[] Zombi_ForbiddenItems = {57};
    public static int[] Zombi_GuardianCrystalPositionLoc = {147448, 23656, -1984};
    public static int[] Zombi_RB_PositionLoc = {147464, 32072, -2480};
    public static int[][] Zombi_ZombiRestartLoc = {{147448, 31112, -2464}};
    public static int[][] Zombi_PlayerRestartLoc = {{147464, 23688, -1984}};
    public static int[][] Zombi_ZombiBuffList = {{7029, 1}};

    public static int Zombi_VisualId = 20001;
    public static int Zombi_RbSpawnTimer = 60;
    public static int Zombi_BattleTime = 600;
    public static int Zombi_PrepareTime = 5;
    public static int Zombi_ChanceTransform = 50;
    public static int Zombi_AttackChanceTransform = 3;
    public static int Zombi_GuardianCrystalID = 31690;
    public static int Zombi_RB_ID = 20001;
    public static float Zombi_GuardianCrystalModMDeff = 1;
    public static float Zombi_GuardianCrystalModPDeff = 1;
    public static float Zombi_GuardianCrystalModHp = 1;

    public static boolean PlayerSubClassOverlord = false;
    public static boolean PlayerSubClassWarsmith = false;
    public static boolean Attainment13_Msg3 = false;

    public static int[] HuntinRB_NotSelectedRB = {};
    public static int[] HuntinRB_TimeToStart = {00, 00};
    public static String HuntinRB_TimeToStartCron = "00 18,20 * * *";
    public static long[] HuntinRB_RevardKill = {57, 100, 100};
    public static int[] HuntinRB_MobLevelRnd = {80, 85};
    public static int HuntinRB_PartyCountReward = 3;
    public static int HuntinRB_TimeToStartBefore = 600;
    public static boolean EnableCustomBaseClass = false;
    public static int[] RaidToPvpFlag = {};
    public static int[][] EnchantNormalCustomItem = {};
    public static int[][] EnchantSafeCustomItem = {};
    // -----------------------
    public static boolean EnableBots = false;
    public static boolean WriteBotsAi = false;
    public static boolean BotsAiFile = false;
    public static boolean BotsAiLoadAgain = true;
    public static int BotsAiRecTime = 30;
    public static int BotsAiMaxActions = 100;
    public static int BotsAiMinActions = 10;
    public static long BotsAiMaxTime = 20000;
    public static int BotsMinLevel = 1;
    public static int BotsMaxLevel = 80;
    public static boolean BotsNoWriteHero = true;
    public static boolean BotsNoWriteGM = true;
    public static boolean BotsSpawn = false;
    public static int BotsSpawnType = 2;
    public static int BotsStartSpawnInterval = 300;
    public static int BotsNextSpawnInterval = 300;
    public static int BotsSpawnCount = 100;
    public static boolean BotsSpawnKeep = false;
    public static boolean BotsStartLocRnd = false;
    public static boolean BotsSpawnAiRnd = false;
    public static boolean BotsDelete = false;
    public static boolean BotsNamesLoadAgain = true;
    public static boolean BotsNameRnd = true;
    public static boolean BotsUsedNames = false;
    public static int[] BotsRestrictZones = {};
    public static int[] BotsRestrictEquipment = {};
    public static int[] BotsRestrictSkills = {};
    public static int[] BotsStartItems = {};
    public static boolean BotsBuffs = true;
    public static int[] BotsBuffsMage = {};
    public static int[] BotsBuffsFighter = {};
    public static int BotsEnchantMax = 20;
    public static int BotsSpawnIntervalMin = 1;
    public static int BotsSpawnIntervalMax = 5;
    public static int BotsUnspawnIntervalMin = 1000;
    public static int BotsUnspawnIntervalMax = 5000;
    public static int BotsFirstActionMin = 1000;
    public static int BotsFirstActionMax = 5000;
    public static int BotsLifeCycleMin = 1;
    public static int BotsLifeCycleMax = 5;
    public static boolean BotsNoble = true;
    public static String BotsTitle = "";
    public static double BotsTitleChance = 3;
    public static double BotsFemale = 0;
    public static boolean BotsSort = false;
    public static boolean BotsCanJoinClan = false;
    public static double BotsChanceJoinClan = 5;
    public static double BotsChanceRefuseClan = 30;
    public static boolean BotsCanJoinParty = false;
    public static double BotsChanceJoinParty = 10;
    public static double BotsChanceRefuseParty = 30;
    public static boolean BotsWriteAttack = false;
    public static boolean BotsStopActions = false;
    public static boolean BotsCanSay = false;
    public static double BotsSayChance = 0.5;
    public static double BotsShoutChance = 0.3;
    public static boolean BotsRemoveSay = false;
    public static boolean BotsSayRnd = true;
    public static String BotsAccount = "bots_players";

    public static boolean EnableSkillLog = false;
    public static int UpdateEffectIconsInterval = 100;
    public static byte UseClientLangRuId = 1;
    public static byte UseClientLangEngId = 0;
    public static boolean SetOverAggrToRb = true;
    public static int[] NoSetOverAggrToRb = {};

    public static boolean EnablePtsPlayerStat = true;
    public static boolean ENABLED = false;

    public static boolean EnableDropCalculator = false;
    public static boolean DropCalculatorFriend = false;
    public static String[] DropCalculator_GradeIcon = {"", "<img src=\"L2UI_CT1.Icon_DF_ItemGrade_D\" width=16 height=16>", "<img src=\"L2UI_CT1.Icon_DF_ItemGrade_C\" width=16 height=16>", "<img src=\"L2UI_CT1.Icon_DF_ItemGrade_B\" width=16 height=16>", "<img src=\"L2UI_CT1.Icon_DF_ItemGrade_A\" width=16 height=16>", "<img src=\"L2UI_CT1.Icon_DF_ItemGrade_S\" width=16 height=16>", "<img src=\"L2UI_CT1.Icon_DF_ItemGrade_S80\" width=16 height=16>", "<img src=\"L2UI_CT1.Icon_DF_ItemGrade_S84\" width=16 height=16>"};
    public static int[] DROP_CALCULATOR_DISABLED_TELEPORT = {};
    public static boolean AutoSetQuest350 = false;
    public static boolean RestoreSubEffects = true;

    public static ZoneType[] TradeZoneType = {};

    public static boolean EnableHaProxy = false;
    public static boolean EnableHaProxy2 = false;
    public static boolean CanStartDuelInReflect = false;
    public static int SaveableEffectTime = 1000;
    public static boolean OtherEventUseBuffer = false;

    public static int[] _254_LegendaryTalesCustomReward = {};

    public static int CardsItemTake = 4037;
    public static int[] CardsJokerChance = {1};
    public static int CardsDelay = 1200;

    public static float[] CardsRewardRate = {1};
    public static float[] CardsJokerRation = {1};
    public static float[] CardsCoincidenceRation2 = {1};
    public static float[] CardsCoincidenceRation3 = {1};
    public static float[] CardsCoincidenceRation4 = {1};
    public static int[] CardsJokerAdd = {1};

    public static boolean CardsAnounceToAll = false;

    public static int _254_LegendaryTalesCustomRewardChance = 100;

    public static boolean TestAtomicState = false;
    public static boolean NewGeoEngine = false;
    public static boolean NewGeoEngineTest = false;
    public static int PathFindDiffZ = 1024; // 256
    public static int CollisionSize = 1;
    public static boolean AllowFollowAttack = true;

    public static int[] RouletteNpcAnons = {};
    public static long[] RouletteNpcPrice = {57, 1};
    public static double[][] RouletteNpcRewardRnd = {{57, 1, 90}, {57, 10, 90}};
    public static long[][] RouletteNpcRewardLimit = {};

    public static boolean EnableClanWarDamageBonus = false;
    public static double ClanWarPhysDamageMod = 1.;
    public static double ClanWarPhysSkillDamageMod = 1.;
    public static double ClanWarMagicDamageMod = 1.;
    public static int ClanWarMailLife = 24;
    public static String ClanWarMailTopic = "";
    public static String ClanWarMailBody = "";

    public static boolean AutoLearnClanSkill = false;

    public static long ShoutChatLaunched = 5000;
    public static long TradeChatLaunched = 5000;
    public static boolean EnableSkillLearnToBbs = false;
    /*************************** BattleForCastle.properties ***************************/
    public static int[] BattleForCastleStartTime = {-1};
    public static String BattleForCastleCronStart = "* * * * *";

    public static int[] BattleForCastleForbiddenItems = {57};
    public static int[] BattleForCastleTimeProfessions = {57};

    public static long[] BattleForCastleRewardWiner = {57, 1000};
    public static long[] BattleForCastleCastRewardClanLeader = {57, 10, 100};
    public static long[] BattleForCastleCastRewardClanMembers = {57, 10, 100};
    public static long[] BattleForCastleCastRewardClanTaker = {57, 10, 100};
    public static long[] BattleForCastleKillReward = {57, 10, 100};
    public static long[] BattleForCastleProfessionsReward = {57, 10, 100};

    public static int BattleForCastleAnnonce = 1800;
    public static int BattleForCastleRewardTime = 300;
    public static int BattleForCastleBattleTime = 60;
    public static int BattleForCastleDefenderResTime = 30000;
    public static boolean BattleForCastleOlympiadItems = false;
    public static boolean BattleForrCastlePartyRandomReward = false;
    public static int[][] BattleForCastleDefenderLocation =
            {
                    {147700, 4608, -2784},
                    {147705, 4865, -2784},
                    {147200, 4865, -2784},
                    {147200, 4350, -2784},
                    {147705, 4350, -2784}
            };

    public static int[][] BattleForCastleAttackerLocation =
            {
                    {146494, 30584, -2420, 0},
                    {146038, 30519, -2420, 0},
                    {148112, 30439, -2420, 0},
                    {148565, 30463, -2420, 0},
                    {150227, 29104, -2420, 0},
                    {144626, 29145, -2420, 0},
                    {144635, 26664, -2220, 0},
                    {144526, 24661, -2100, 0},
                    {144559, 22835, -2100, 0},
                    {145686, 21114, -2100, 0},
                    {148946, 21121, -2070, 0},
                    {148010, 27996, -2256, 0},
                    {147970, 27040, -2191, 0},
                    {146814, 27108, -2189, 0},
                    {146819, 28063, -2252, 0}
            };
    public static double BattleForCastleDorsHP = 1;
    public static double BattleForCastleDorsPDeff = 1;
    public static double BattleForCastleDorsMDeff = 1;

    public static long[][] AnnounceToGmMaxItem = {};

    public static String RussianVote = "Тест";
    public static String EnglishVote = "Test";
    public static String VoteLogName = "test";
    public static String CheckVotePlayers = "none";
    public static String DateVoteEnd = "03.03.2019 22:00:00";

    public static int PetControlLevelDiff = 20;
    public static boolean BufferRestoreOnlyPeace = false;

    public static int CancelWarClanRep = 0;
    public static boolean NotSetFameDeadPlayer = false;

    public static int[] VidakSpawnRb = {20001, 20001, 20001};
    public static int[] VidakSpawnNpc = {31264, 32239};

    public static int AntharasCustomRewardPlayerCount = 0;
    public static long[] AntharasCustomReward = {57, 1};
    public static long[][] AntharasCustomRewardRnd = {};

    public static int ValakasCustomRewardPlayerCount = 0;
    public static long[] ValakasCustomReward = {57, 1};
    public static long[][] ValakasCustomRewardRnd = {};

    public static int BaiumCustomRewardPlayerCount = 0;
    public static long[] BaiumCustomReward = {57, 1};
    public static long[][] BaiumCustomRewardRnd = {};

    public static int SummonDisappearRange = 2500;
    public static int SummonInteractDistance = 0;

    public static boolean MultiSellListNoFix = false;
    public static int[] IgnorUnTradebleDurable = {};
    public static int[] IgnorUnEnchantDurable = {};
    public static boolean BotReportEnableReward = true;

    public static boolean PremiumBufferEnable = false;
    public static int[] PremiumBufferItems = {4037};
    public static int[] PremiumBufferDays = {1};

    public static boolean SeparateSubBlockJudicator = false;
    public static boolean TakeFish_CheckIp = false;
    public static boolean TakeDrop_CheckIp = false;
    public static boolean HuntinFishing_CheckIp = false;

    public static double HitByPoleNextTargetMod = 0.85;
    public static boolean Multi_Delevel = false;
    public static boolean FourGobletsSetItemInAllAggroList = false;

    public static boolean AntharasNeedCC = true;
    public static boolean ValakasNeedCC = true;
    public static boolean AddDonatBonus = false;

    public static double MCritBaseDamageToPlayable = 2.5;
    public static double MCritBaseDamage = 3;
    public static boolean RequestToTeleportInRbSpawn = false;

    public static boolean EnableCustomInterface = true;
    public static boolean TiatDisableCommandChanel = false;

    public static boolean BreakRealTarget = true;
    public static boolean CanMagicReflectOverDamage = true;
    public static int[] ItemsEnchantAsOlf = {21580, 21706};

    public static boolean EnableSkillTargetTest = false;
    public static int[][] EventBoxMagicBuff = {{1323, 1}};
    public static int[][] EventBoxPhisicBuff = {{1323, 1}};

    // Не выдавать ПВП если убиваешь Мембера своего Клана
    public static boolean PvPCheckCheckOnCLAN = false;
    // Не выдавать ПВП если убиваешь чара с одинаковым железом
    public static boolean PvPCheckCheckOnHWID = false;
    // Не выдавать ПВП если убиваешь чара с одинаковым ип
    public static boolean PvPCheckCheckOnIP = false;
    // Не выдавать ПВП Если кто то получал с чара уже ПВП менее чем TimeLastPvP (в секундах)
    public static int TimeLastPvP = -1;

    public static boolean SERVICES_EXCHANGE_EQUIP = true;
    public static int SERVICES_EXCHANGE_EQUIP_ITEM = 57;
    public static int SERVICES_EXCHANGE_EQUIP_ITEM_PRICE = 1000;
    public static int SERVICES_EXCHANGE_UPGRADE_EQUIP_ITEM = 57;
    public static int SERVICES_EXCHANGE_UPGRADE_EQUIP_ITEM_PRICE = 1000;
    public static long[] Buffer_PremiumBuffPrice = {57, 1};
    public static int[][] Buffer_PremiumBuff = {};

}