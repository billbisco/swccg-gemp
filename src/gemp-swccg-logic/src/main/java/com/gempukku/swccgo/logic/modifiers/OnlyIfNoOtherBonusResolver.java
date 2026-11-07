package com.gempukku.swccgo.logic.modifiers;

/**
 * Resolves bonuses that apply only if no other bonus of the same kind applies (modifiers marked with
 * {@link Modifier#setOnlyIfNoOtherBonus(boolean)}), without re-evaluating modifiers recursively.
 * <p>
 * Used while summing one value (for example a Force drain amount or a forfeit value): every modifier that
 * would apply is passed to {@link #defer(Modifier, float)}. Ordinary modifiers are applied by the caller as
 * usual; a conditional bonus is held back. After the loop, {@link #resolve(ModifierCollector)} returns the
 * held-back bonus to add: nothing if any ordinary bonus (amount &gt; 0) applied, otherwise the largest single
 * conditional bonus. Conditional bonuses never count as "another bonus" for each other, and at most one of
 * them applies, since once one applies the others would no longer be the only bonus.
 */
public class OnlyIfNoOtherBonusResolver {
    private boolean _otherBonusApplied;
    private Modifier _bestConditionalBonus;
    private float _bestConditionalAmount;

    /**
     * Records a modifier that would apply to the value being calculated.
     * @param modifier the modifier
     * @param amount the amount the modifier would add
     * @return true if the modifier is a conditional bonus held back for {@link #resolve(ModifierCollector)}
     * (the caller must not apply it now), false if the caller applies it as usual
     */
    public boolean defer(Modifier modifier, float amount) {
        if (modifier.isOnlyIfNoOtherBonus() && amount > 0) {
            if (_bestConditionalBonus == null || amount > _bestConditionalAmount) {
                _bestConditionalBonus = modifier;
                _bestConditionalAmount = amount;
            }
            return true;
        }
        if (amount > 0) {
            _otherBonusApplied = true;
        }
        return false;
    }

    /**
     * Gets the amount of conditional bonus to add after all modifiers were recorded.
     * @param modifierCollector collector the applied conditional bonus is added to
     * @return the amount to add, or 0 if no conditional bonus applies
     */
    public float resolve(ModifierCollector modifierCollector) {
        if (_otherBonusApplied || _bestConditionalBonus == null) {
            return 0;
        }
        modifierCollector.addModifier(_bestConditionalBonus);
        return _bestConditionalAmount;
    }
}
