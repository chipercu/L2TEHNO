<?xml version="1.0" encoding="utf-8"?>
<list>
     <!-- partyType определяет стиль поведения группы
        mage - будут кайтить, дизейблить хилеров, убивать нюкеров
        archer - будут кайтить, убивать ближайший к группе таргет
        limit - будут сливаться на лимиты и убивать
        dagger - будут искать хилеров, сапортов и магов и убивать их в ассист
        stop - будут агрить, дебафать, дизейблить противника
        suspended - будут стоять, иногда бафать/хилиться
	--> 
	<!-- ID мемберов это obj_id из таблицы characters -->
	<!-- partyCooldown - время простоя группы в городе, когда все члены группы умерли. В секундах. -->
	<!-- handicap - сила ботов. Attack - наносимый урон, Defence - уровень защиты. По умолчанию 100% -->
	<!-- partyClan это ID клана (0 без клана) группа не будет бить сокланов -->
	<PartyObject partyName="SoulRise">
		<config>
			<set stat="partyId" val="1" />
			<set stat="partyClan" val="0" />
			<set stat="isSpawnEnabled" val="true" />
			<set stat="regroupToLeaderChance" val="1" />
			<set stat="regroupToPlaceChance" val="1" />
			<set stat="randomMoveChance" val="25" />
			<set stat="partyType" val="mage" />
			<set stat="handicapAttack" val="100" />
			<set stat="handicapDefence" val="100" />
			<set stat="partyCooldown" val="25" />
			<set stat="class_list" val="95;103;103;103;107;100;97;97;90" />
		</config>
		<!-- Размер группы должен быть больше 1 и меньше 10! -->
		<!-- Группа спавнится по списку координат -->
		<coord_s x="143224" y="145880" z="-12032"/> 
		<coord_s x="148248" y="146152" z="-12280"/> 
		<!-- Группа делает регруп к ближайшей из списка координате -->
		<coord_r x="148232" y="143752" z="-12224"/>
		<coord_r x="143448" y="142872" z="-11880"/>  
		<!-- Группа спавнится в мирной локации по списку координат -->
		<coord_p x="148072" y="144968" z="-12258"/>  
	</PartyObject>
 </list>