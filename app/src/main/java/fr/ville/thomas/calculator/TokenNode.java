package fr.ville.thomas.calculator;

import java.util.ArrayList;

/**
 * Created by Thomas on 30/08/2016.
 */
public class TokenNode {
    private char m_op;
    private float m_value;

    private ArrayList<TokenNode> m_childs;

    private boolean isOperator = false;

    TokenNode(char p_operation){
        m_childs = new ArrayList<>();
        m_op = p_operation;
        isOperator = true;
    }
    TokenNode(float p_value){
        m_childs = new ArrayList<>();
        m_value = p_value;
        isOperator = false;
    }

    public boolean isOperator(){
        return isOperator;
    }

    public void addChild(TokenNode p_child){
        m_childs.add(p_child);
    }

    public boolean isComplete(){
        if(m_childs.size()==2)
            return true;

        return false;
    }

    /* + et - ont une priorité 1
    * et / ont une priorité 0
     */
    public int getPriority(){
        if(m_op == '+' || m_op =='-')
            return 1;
        else
            return 0;
    }

    public String toString(){
        if(isOperator())
            return String.valueOf(m_op);
        else
            return String.valueOf(m_value);
    }

    public float recursiveComputeValue() {
        // Si le noeud est une valeur, on renvoit la valeur
        if(!isOperator())
            return getValue();

        // Sinon on renvoit le résultat de l'opération entre les deux fils
        switch(getOperator()){
            case '+':
                return getLeftChild().recursiveComputeValue() + getRightChild().recursiveComputeValue();
            case '-':
                return getLeftChild().recursiveComputeValue() - getRightChild().recursiveComputeValue();
            case '*':
                return getLeftChild().recursiveComputeValue() * getRightChild().recursiveComputeValue();
            case '/':
                return getLeftChild().recursiveComputeValue() / getRightChild().recursiveComputeValue();
        }

        // TODO : Erreur non gérée. Mauvais opérateur
        return 0.0f;
    }

    public char getOperator() {
        return m_op;
    }
    public float getValue(){
        return m_value;
    }

    public TokenNode getLeftChild(){
        return m_childs.get(0);
    }
    public TokenNode getRightChild() {
        return m_childs.get(1);
    }
}
