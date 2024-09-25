package objects;

public abstract class AttackEntity {

	protected int damage;
	
	public AttackEntity(int damage) {
		this.damage = damage;
	}
	
	public void dealDamage(GameEntity target) {
        if (target != null) {
            target.takeDamage(damage);
        }
    }	

    public int getDamage() {
        return damage;
    }
}
