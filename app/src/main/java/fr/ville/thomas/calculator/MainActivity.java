package fr.ville.thomas.calculator;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView m_resultView;
    private TextView m_tmpResultView;
    private String m_currentValue = "";
    private String m_tmpValue = "";

    private LinearLayout m_mainLayout;
    private TextView m_featuresView;

    private int m_activeView = 0; // 0 = calcul, 1 = détails, 2 = fonctionnalités
    private ListView m_detailListView; // La liste physiquement affichée
    private ArrayList<SpannableString> m_stepsList; // Les éléments de la liste (étapes du calcul)
    ArrayAdapter<SpannableString> m_detailsListAdapter; // Adapter qui feed la liste avec les éléments

    private ArrayList<TokenNode> m_mathTree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_mathTree = new ArrayList<>();

        m_detailListView = (ListView) findViewById(R.id.detailListView);
        m_stepsList = new ArrayList<>();
        m_detailsListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, m_stepsList);
        // Associe l'adapter à la liste physique
        m_detailListView.setAdapter(m_detailsListAdapter);

        m_resultView = (TextView) findViewById(R.id.resultView);
        m_tmpResultView = (TextView) findViewById(R.id.tmpResultView);
        m_mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        m_featuresView = (TextView) findViewById(R.id.featuresView);

        // Interprète la liste des features au format HTML
        Spanned result;
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(getString(R.string.features_list),Html.FROM_HTML_MODE_LEGACY, null, null);
        } else {
            result = Html.fromHtml(getString(R.string.features_list));
        }*/
        result = Html.fromHtml(getString(R.string.features_list));
        m_featuresView.setText(result);

        findViewById(R.id.button0).setOnClickListener(this);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);
        findViewById(R.id.button8).setOnClickListener(this);
        findViewById(R.id.button9).setOnClickListener(this);
        findViewById(R.id.buttonPlus).setOnClickListener(this);
        findViewById(R.id.buttonMinus).setOnClickListener(this);
        findViewById(R.id.buttonMulti).setOnClickListener(this);
        findViewById(R.id.buttonDivide).setOnClickListener(this);
        findViewById(R.id.buttonDot).setOnClickListener(this);
        findViewById(R.id.buttonEqual).setOnClickListener(this);
        findViewById(R.id.buttonDel).setOnClickListener(this);
        findViewById(R.id.buttonClear).setOnClickListener(this);
        findViewById(R.id.buttonLeftPar).setOnClickListener(this);
        findViewById(R.id.buttonRightPar).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button0:
                addElement("0");
                computeResult();
                break;
            case R.id.button1:
                addElement("1");
                computeResult();
                break;
            case R.id.button2:
                addElement("2");
                computeResult();
                break;
            case R.id.button3:
                addElement("3");
                computeResult();
                break;
            case R.id.button4:
                addElement("4");
                computeResult();
                break;
            case R.id.button5:
                addElement("5");
                computeResult();
                break;
            case R.id.button6:
                addElement("6");
                computeResult();
                break;
            case R.id.button7:
                addElement("7");
                computeResult();
                break;
            case R.id.button8:
                addElement("8");
                computeResult();
                break;
            case R.id.button9:
                addElement("9");
                computeResult();
                break;
            case R.id.buttonPlus:
                addElement("+");
                computeResult();
                break;
            case R.id.buttonMinus:
                addElement("-");
                computeResult();
                break;
            case R.id.buttonDivide:
                addElement("/");
                computeResult();
                break;
            case R.id.buttonMulti:
                addElement("*");
                computeResult();
                break;
            case R.id.buttonDot:
                addElement(".");
                computeResult();
                break;
            case R.id.buttonLeftPar:
                addElement("(");
                computeResult();
                break;
            case R.id.buttonRightPar:
                addElement(")");
                computeResult();
                break;
            case R.id.buttonDel:
                if(m_currentValue.length() > 0) {
                    m_currentValue = m_currentValue.substring(0, m_currentValue.length() - 1);
                    computeResult();
                }
                break;
            case R.id.buttonClear:
                m_currentValue = "";
                m_tmpValue = "";
                m_resultView.setText("");
                m_tmpResultView.setText("");
                resetResultColor();
            case R.id.buttonEqual:
                // Met tmp dans current et clear tmp
                m_currentValue = m_tmpValue;
                m_tmpValue = "";
                m_resultView.setText(m_currentValue);
                m_tmpResultView.setText("");
                break;
        }
        m_resultView.setText(m_currentValue);
    }

    private void addElement(String number){
        if(m_currentValue.equals("0"))
            m_currentValue = "";

        m_currentValue += number;
    }

    private void computeResult() {
        String buffer = "";
        m_mathTree.clear();
        m_stepsList.clear();

        if(m_currentValue.equals("")){
            m_tmpResultView.setText("0");
            m_currentValue = "0";
            resetResultColor();
        }

        char lastC = ' ';
        for(char c : m_currentValue.toCharArray()){
            if(isOperator(c)){
                // S'il y avait un nombre dans le buffer, on l'ajoute
                if(buffer.length() != 0) {
                    try { // Cas particuliers (--)
                        m_mathTree.add(new TokenNode(Float.valueOf(buffer)));
                    }catch(Exception e){
                        onSyntaxError();
                        return;
                    }
                    buffer = "";
                }
                // Si on avait un opérateur avant et que l'opérateur est un - on part du principe que c'est un nombre négatif
                // Attention : si on ferme une parenthèse, il faut considérer le - comme un opérateur
                else if(c == '-' && lastC != ')'){
                    buffer += c;
                    lastC = c;
                    continue;
                }
                // On ajoute le token
                m_mathTree.add(new TokenNode(c));
                lastC = c;
            }
            else // Si on n'est pas sur un opérateur, on remplit le buffer
            {
                buffer += c;
                lastC = c;
            }
        }

        // S'il y avait un nombre à la fin, on le traite ici
        if(buffer.length() != 0) {
            try {
                m_mathTree.add(new TokenNode(Float.valueOf(buffer)));
            }catch(Exception e){
                onSyntaxError();
                return;
            }
        }

        // Commence à construire l'arbre
        // Tant qu'il y a plus d'un node dans la liste, on continue à construire l'arbre
        int priority = 0;
        boolean atLeastOneOpComputed = false;
        while(m_mathTree.size() != 1){
            atLeastOneOpComputed = false;
            // On fait une passe, on cherche les opérateurs pour leur ajouter des fils
            for(int i = 0; i < m_mathTree.size(); i++){
                // Si on est sur un opérateur, on ajoute ses voisins de droite et gauche en tant que fils
                // Check les limites et que l'opérateur n'est pas déjà plein
                if(i>0 && i < m_mathTree.size()-1 && m_mathTree.get(i).isOperator() &&
                        m_mathTree.get(i).getOperator() != '(' &&
                        m_mathTree.get(i).getOperator() != ')' &&
                        m_mathTree.get(i).getPriority() == priority &&
                        !m_mathTree.get(i).isComplete() &&
                        m_mathTree.get(i+1).getOperator() != '(' && // Empêche de prendre des parenthèses en fils
                        m_mathTree.get(i+1).getOperator() != ')' &&
                        m_mathTree.get(i-1).getOperator() != '(' &&
                        m_mathTree.get(i-1).getOperator() != ')'
                        ){

                    OnNewComputeStep(i);

                    m_mathTree.get(i).addChild(m_mathTree.get(i-1));
                    m_mathTree.get(i).addChild(m_mathTree.get(i+1));

                    // On fait attention à supprimer le deuxième en premier pour pouvoir supprimer le premier sans se soucier de l'indice
                    m_mathTree.remove(i+1);
                    m_mathTree.remove(i-1);
                    i--;
                    atLeastOneOpComputed = true;
                }
                // Si on est un élément et qu'on est entouré de parenthèses, on peut les virer
                else if(i>0 && i < m_mathTree.size()-1 &&
                        (
                                ( // Opérateur complet
                                        m_mathTree.get(i).isOperator() && m_mathTree.get(i).isComplete()
                                ) || // Ou nombre tout simplement
                                !m_mathTree.get(i).isOperator()
                        ) && // Entouré par des parenthèses
                        m_mathTree.get(i-1).getOperator() == '(' &&
                        m_mathTree.get(i+1).getOperator() == ')'){
                    OnNewComputeStep(i);
                    // On enlève les parenthèses en prenant toujours soin d'enlever la plus éloignée en premier
                    m_mathTree.remove(i+1);
                    m_mathTree.remove(i-1);
                    i--;
                    // Reset la priorité de manière à recommencer le processus pour les opérateurs extérieurs aux parenthèses
                    priority = 0;
                    atLeastOneOpComputed = true;
                }
            }
            // Si on n'a fait aucun opérateur, on passe à la priorité suivante d'opérateurs
            if(!atLeastOneOpComputed){
                priority++;
                // Si on a une erreur de syntaxe (nombre manquant)
                if(priority == 2) {
                    onSyntaxError();
                    return;
                }
            }
        }

        // Dernière étape de calcul : le résultat
        OnNewComputeStep(0);
        try
        {
            float value = m_mathTree.get(0).recursiveComputeValue();

            // Si on trouve l'infinie, on affiche une erreur
            if(Float.isInfinite(value))
                throw new Exception();

            m_tmpValue = String.valueOf(value);
        }catch(Exception e)
        {
            onSyntaxError();
            return;
        }

        resetResultColor();
        m_detailsListAdapter.notifyDataSetChanged();
        m_tmpResultView.setText(m_tmpValue);
    }

    private boolean isOperator(char c){
        if(c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')')
            return true;
        else
            return false;
    }

    private void resetResultColor(){
        m_resultView.setTextColor(Color.GRAY);
    }
    private void onSyntaxError(){
        m_resultView.setTextColor(Color.RED);
    }

    private void OnNewComputeStep(int i){
        // Ajoute une étape à la liste des étapes
        String step = "";
        int leftBoundary = 0, rightBoundary = 0;
        for(int j = 0; j < m_mathTree.size(); j++){
            // Le début de l'opérande de gauche est le début de l'expression en cours de calcul
            if(j == i-1)
                leftBoundary = step.length();

            // On peut calculer la valeur de l'opération
            if(m_mathTree.get(j).isOperator() && m_mathTree.get(j).isComplete()){
                step += String.valueOf(m_mathTree.get(j).recursiveComputeValue());

                // La fin de l'opérande de droite est la fin de l'expression en cours de calcul
                if(j == i+1)
                    rightBoundary = step.length();
            }
            else {
                step += m_mathTree.get(j).toString();
                // La fin de l'opérande de droite est la fin de l'expression en cours de calcul
                if (j == i + 1)
                    rightBoundary = step.length();
            }
        }
        SpannableString spanText = new SpannableString(step);
        spanText.setSpan(new ForegroundColorSpan(Color.RED), leftBoundary, rightBoundary, 0);
        m_stepsList.add(spanText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Change d'état en fonction du bouton sur lequel on a appuyé
        if (id == R.id.show_details) {
            if(m_activeView == 1)
                m_activeView = 0;
            else
                m_activeView = 1;
        }
        else if(id == R.id.show_features){
            if(m_activeView == 2)
                m_activeView = 0;
            else
                m_activeView = 2;
        }

        // Effectue le changement effectif dans le layout
        switch(m_activeView){
            case 0: // Affiche la vue de calcul
                m_mainLayout.setVisibility(View.VISIBLE);
                m_detailListView.setVisibility(View.GONE);
                m_featuresView.setVisibility(View.GONE);
                break;
            case 1: // Affiche les détails du calcul
                m_detailListView.setVisibility(View.VISIBLE);
                m_mainLayout.setVisibility(View.GONE);
                m_featuresView.setVisibility(View.GONE);
                break;
            case 2: // Affiche les fonctionnalités
                m_featuresView.setVisibility(View.VISIBLE);
                m_mainLayout.setVisibility(View.GONE);
                m_detailListView.setVisibility(View.GONE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
