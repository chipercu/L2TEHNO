package utils_soft.NpcEditor;

import l2open.database.Filter;
import l2open.database.Resource;
import l2open.database.ResourceProvider;
import l2open.database.schemes.builders.*;
import l2open.database.schemes.resources.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;

import java.util.List;

public class NpcEditorResource {

    private static NpcEditorResource resource;

    private final Resource<NpcResource, NpcBuilder> npcResource;
    private final Resource<NpcElementResource, NpcElementBuilder> npcElementResource;
    private final Resource<DroplistResource, DroplistBuilder> dropListResource;
    private final Resource<NpcskillsResource, NpcskillsBuilder> npcSkillsResource;
    private final Resource<SpawnlistResource, SpawnlistBuilder> spawnListResource;

    private NpcEditorResource() {
        this.npcResource = new ResourceProvider<>(NpcResource.class);
        this.npcElementResource = new ResourceProvider<>(NpcElementResource.class);
        this.dropListResource = new ResourceProvider<>(DroplistResource.class);
        this.npcSkillsResource = new ResourceProvider<>(NpcskillsResource.class);
        this.spawnListResource = new ResourceProvider<>(SpawnlistResource.class);
    }

    public static NpcEditorResource getInstance() {
        if (resource == null){
            resource = new NpcEditorResource();
        }
        return resource;
    }

    public Resource<NpcResource, NpcBuilder> getNpcResource() {
        return npcResource;
    }

    public Resource<NpcElementResource, NpcElementBuilder> getNpcElementResource() {
        return npcElementResource;
    }

    public Resource<DroplistResource, DroplistBuilder> getDropListResource() {
        return dropListResource;
    }

    public Resource<NpcskillsResource, NpcskillsBuilder> getNpcSkillsResource() {
        return npcSkillsResource;
    }

    public Resource<SpawnlistResource, SpawnlistBuilder> getSpawnListResource() {
        return spawnListResource;
    }

    public List<NpcResource> getNpcFilteredList(String filter, String filterValue, int limit, int offset){
        switch (filter) {
            case "npcname": return getNpcResource().findList(new Filter().LIKE(NpcResource.NAME, filterValue).ORDER_BY(NpcResource.ID).LIMIT(limit).OFFSET(offset));
            case "npcid": return getNpcResource().findList(new Filter().LIKE(NpcResource.ID, filterValue).ORDER_BY(NpcResource.ID).LIMIT(limit).OFFSET(offset));
            case "npctype": return getNpcResource().findList(new Filter().LIKE(NpcResource.TYPE, filterValue).ORDER_BY(NpcResource.ORDINAL).LIMIT(limit).OFFSET(offset));
            default: return getNpcResource().findList(new Filter().ORDER_BY(NpcResource.ID).LIMIT(limit).OFFSET(offset));
        }
    }

    public NpcResource getNpcById(int npcId){
        return NpcEditorResource.getInstance().getNpcResource().find(new Filter().WHERE(NpcResource.ID, npcId));
    }

    public NpcElementResource getNpcElementById(int npcId){
        return NpcEditorResource.getInstance().getNpcElementResource().find(new Filter().WHERE(NpcElementResource.ID, npcId));
    }

    public SpawnlistResource getNpcSpawn(L2NpcInstance npc){
        final Location loc = npc.getSpawnedLoc();
        return NpcEditorResource.getInstance().getSpawnListResource().find(new Filter()
                .WHERE(SpawnlistResource.NPC_TEMPLATEID, npc.getNpcId())
                .AND(SpawnlistResource.LOCX, loc.x)
                .AND(SpawnlistResource.LOCY, loc.y)
                .AND(SpawnlistResource.LOCZ, loc.z)
                .AND(SpawnlistResource.REFLECTION, npc.getReflectionId())
        );
    }

}
