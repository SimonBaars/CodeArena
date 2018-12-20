package nl.sandersimon.clonedetection.monster.codespider;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import nl.sandersimon.clonedetection.model.CloneClass;
import nl.sandersimon.clonedetection.monster.CodeEntity;

public class EntityCodeSpider extends CodeEntity
{
    private static final DataParameter<Byte> CLIMBING = EntityDataManager.<Byte>createKey(EntityCodeSpider.class, DataSerializers.BYTE);

    public EntityCodeSpider(World worldIn, CloneClass cloneClass)
    {
        super(worldIn, cloneClass);
        //setAbsorptionAmount(0.05F*getRepresents().volume());
        //entityCollisionReduction = 0.05F*getRepresents().volume();
        //System.out.println("SET COLISION TO "+entityCollisionReduction);
        
    }
    
    public EntityCodeSpider(World worldIn)
    {
        super(worldIn);
    }

    public static void registerFixesSpider(DataFixer fixer)
    {
        EntityLiving.registerFixesMob(fixer, EntityCodeSpider.class);
    }

    protected void initEntityAI()
    {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(4, new EntityCodeSpider.AICodeSpiderAttack(this));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
        //this.targetTasks.addTask(2, new EntityCodeSpider.AICodeSpiderTarget(this, EntityPlayer.class));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, false, false));
    }
    
  /*  @Override
    public void setPosition(double par1, double par2, double par3) {
    	AxisAlignedBB b = this.getEntityBoundingBox();
    	double boxSX = b.maxX - b.minX;
    	double boxSY = b.maxY - b.minY;
    	double boxSZ = b.maxZ - b.minZ;
    	this.setEntityBoundingBox(new AxisAlignedBB(posX - boxSX/2D, posY, posZ - boxSZ/2D, posX + boxSX/2D, posY + boxSY, posZ + boxSZ/2D));
    }*/

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return (double)(this.height * 0.5F);
    }

    /**
     * Returns new PathNavigateGround instance
     */
    protected PathNavigate createNavigator(World worldIn)
    {
        return new PathNavigateClimber(this, worldIn);
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(CLIMBING, Byte.valueOf((byte)0));
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
    }

    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_SPIDER_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_SPIDER_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_SPIDER_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTableList.ENTITIES_SPIDER;
    }

    /**
     * Sets the Entity inside a web block.
     */
    public void setInWeb()
    {
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    public boolean isPotionApplicable(PotionEffect potioneffectIn)
    {
        return potioneffectIn.getPotion() == MobEffects.POISON ? false : super.isPotionApplicable(potioneffectIn);
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
     */
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
    {
        livingdata = super.onInitialSpawn(difficulty, livingdata);

        if (livingdata == null)
        {
            livingdata = new EntityCodeSpider.GroupData();

            if (this.world.getDifficulty() == EnumDifficulty.HARD && this.world.rand.nextFloat() < 0.1F * difficulty.getClampedAdditionalDifficulty())
            {
                ((EntityCodeSpider.GroupData)livingdata).setRandomEffect(this.world.rand);
            }
        }

        if (livingdata instanceof EntityCodeSpider.GroupData)
        {
            Potion potion = ((EntityCodeSpider.GroupData)livingdata).effect;

            if (potion != null)
            {
                this.addPotionEffect(new PotionEffect(potion, Integer.MAX_VALUE));
            }
        }

        return livingdata;
    }

    public float getEyeHeight()
    {
        return 0.65F;
    }

    static class AICodeSpiderAttack extends EntityAIAttackMelee
        {
            public AICodeSpiderAttack(EntityCodeSpider spider)
            {
                super(spider, 1.0D, true);
            }

            /**
             * Returns whether an in-progress EntityAIBase should continue executing
             */
            public boolean shouldContinueExecuting()
            {
                float f = this.attacker.getBrightness();

                if (f >= 0.5F && this.attacker.getRNG().nextInt(100) == 0)
                {
                    this.attacker.setAttackTarget((EntityLivingBase)null);
                    return false;
                }
                else
                {
                    return super.shouldContinueExecuting();
                }
            }

            protected double getAttackReachSqr(EntityLivingBase attackTarget)
            {
                return (double)(4.0F + attackTarget.width);
            }
        }

    static class AICodeSpiderTarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T>
        {
            public AICodeSpiderTarget(EntityCodeSpider spider, Class<T> classTarget)
            {
                super(spider, classTarget, true);
            }

            /**
             * Returns whether the EntityAIBase should begin execution.
             */
            public boolean shouldExecute()
            {
                float f = this.taskOwner.getBrightness();
                return f >= 0.5F ? false : super.shouldExecute();
            }
        }

    public static class GroupData implements IEntityLivingData
        {
            public Potion effect;

            public void setRandomEffect(Random rand)
            {
                int i = rand.nextInt(5);

                if (i <= 1)
                {
                    this.effect = MobEffects.SPEED;
                }
                else if (i <= 2)
                {
                    this.effect = MobEffects.STRENGTH;
                }
                else if (i <= 3)
                {
                    this.effect = MobEffects.REGENERATION;
                }
                else if (i <= 4)
                {
                    this.effect = MobEffects.INVISIBILITY;
                }
            }
        }
    
    public float getScaleFactor() {
    	return entityCollisionReduction;
    }
}