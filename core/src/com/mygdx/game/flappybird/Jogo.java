package com.mygdx.game.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;


public class Jogo extends ApplicationAdapter {

	private SpriteBatch batch;
	//guarda as texturas
	private Texture passaros;
	private Texture fundo;
	private Texture canoAlto;
	private Texture canoBaixo;
	private Texture GameOver;
	private Texture moedaPrata;
	private Texture moedaOuro;
	private Texture logo;


	BitmapFont textPontuacao;// mostra o texto de pontos
	BitmapFont textRenicia; // mostra o reiniciar o game
	BitmapFont textMelhorPontuacao;// mostra a melhor pontuação

	private boolean passouCano = false;// verificar se passou é true or false

	private Random random;// random

	private int pontuacaoMaxima = 0;
	private int pontos = 0;// variavel de pontos
	private int gravidade = 0;// variavel de gravidade
	private int estadojogo = 0;// variavel de estado do jogo
	private int moedapravalor = 0;
	int valor = 1;

	private float variacao = 0; // variação da animação
	private float posicaoInicialVerticalPassaro = 0;// a posição do passaro na vertical
	private float posicaoCanoHorizontal; // a posição do cano horizontal
	private float posicaoCanoVertical; // a posição do cano vertical
	private float larguradispositivo;// a largura do dispositivo
	private float alturadispositivo;// a altura do dispositivo
	private float espacoEntreCanos;// o espaço dos canos
	private float posicaoHorizontalPassaro = 0;
	private float posicaoMOedaouro;
	private float posicaoMOedaPrata;
	private float posicaomoedavetical;


	private ShapeRenderer shapeRenderer;
	//  vars de colisão
	private Circle circuloPassaro;
	private Rectangle retaguloCanoCima;
	private Rectangle retanguloBaixo;
	private Circle ciculoMoedaOuro;
	private Circle ciculoMoedaPrata;

	// sons
	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;
	Sound somMoedas;

	Preferences preferencias;



	@Override
	public void create() {
		inicializaTexuras();// inicialização de texturas
		inicializarObjetos();// inicialização dos objs

	}

	private void inicializarObjetos() {

		batch = new SpriteBatch();
		random = new Random();//random


		alturadispositivo = Gdx.graphics.getHeight();// declara que a altura é a mesma do dispositivo
		larguradispositivo = Gdx.graphics.getWidth();// declara que largura é a mesma do dispositivo
		posicaoInicialVerticalPassaro = alturadispositivo / 2;// pega a metade da altura do dispositivo
		posicaomoedavetical = alturadispositivo / 2;
		posicaoCanoHorizontal = larguradispositivo;// declara que a posição do cano sera a largura do dispositivo
		posicaoMOedaouro = larguradispositivo;
		posicaoMOedaPrata = larguradispositivo;

		espacoEntreCanos = 350;


		textPontuacao = new BitmapFont();// pegando a texto de pontos
		textPontuacao.setColor(Color.WHITE);// deixa ele com cor branca
		textRenicia = new BitmapFont();// pegando o texto de pontos
		textPontuacao.getData().setScale(10);// determina o tamanho do texto

		textRenicia.setColor(Color.GREEN);// deixa ele com cor branca
		textRenicia.getData().setScale(3);// determina o tamanho do texto

		textMelhorPontuacao = new BitmapFont();// pega o texto de pontos
		textMelhorPontuacao.setColor(Color.RED);// deixa ele com cor branca
		textMelhorPontuacao.getData().setScale(3);// determina o tamanho do texto

		// colisões
		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retaguloCanoCima = new Rectangle();
		retanguloBaixo = new Rectangle();
		ciculoMoedaOuro = new Circle();
		ciculoMoedaPrata = new Circle();

		// som do audios
		somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));
		somMoedas = Gdx.audio.newSound(Gdx.files.internal("Som_Moeda.mp3"));


		preferencias = Gdx.app.getPreferences("flappyBird");
		pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);// guarda a maior pontuação

	}

	private void inicializaTexuras() {

		fundo = new Texture("fundo.png");//pega a textura para criar

		// pondo a imagem do passaro e outras

		passaros = new Texture("Bomba.png");
		// pegando texturas do cano
		canoAlto = new Texture("cano_topo_maior.png");
		canoBaixo = new Texture("cano_baixo_maior.png");

		moedaOuro = new Texture("MoedaOuro.png");
		moedaPrata = new Texture("MoedaPrata1.png");

		GameOver = new Texture("game_over.png");
		logo = new Texture("LogoFIm.png");


	}

	@Override
	public void render() {

		verificaEstadojogo();// verifica os estados do jogo
		desenharTexturas();// renderiza as texuras
		detectarColisao();// detecta as colisões dos objetos
		validarPontos();// valida os pontos quando passsa entre os canos


	}

	private void detectarColisao() {

		circuloPassaro.set(50 + passaros.getWidth() / 2f,
				posicaoInicialVerticalPassaro + passaros.getHeight() / 2f,
				passaros.getWidth() / 2f);// colisão do bird

		retanguloBaixo.set(posicaoCanoHorizontal, alturadispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical, canoBaixo.getWidth(), canoBaixo.getHeight());// pondo colisão nos canos

		retaguloCanoCima.set(posicaoCanoHorizontal, alturadispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical, canoAlto.getWidth(), canoAlto.getHeight());// coloca colisão nos canos

		ciculoMoedaPrata.set(posicaoMOedaPrata, alturadispositivo /2 + posicaomoedavetical + moedaPrata.getHeight() / 2f,
				moedaPrata.getWidth() / 2f);

		ciculoMoedaOuro.set(posicaoMOedaouro, alturadispositivo /2 + posicaomoedavetical + moedaOuro.getHeight() / 2f,
				moedaOuro.getWidth() / 2f);

		boolean beteumoedaOuro = Intersector.overlaps(circuloPassaro, ciculoMoedaOuro);
		boolean beteumoedaPrata = Intersector.overlaps(circuloPassaro, ciculoMoedaPrata);
		boolean bateuCanoCima = Intersector.overlaps(circuloPassaro, retaguloCanoCima);// verifica a colisão entre cano e passaro
		boolean bateuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloBaixo);// verifica a colisão entre cano e passaro
		if (bateuCanoBaixo || bateuCanoCima) {
			// se o estado do jogo for 1, ele dispara o som da colisão e muda o estado do jogo para 2
			if (estadojogo == 1) {
				somColisao.play();// dispara o som de colisão
				estadojogo = 2;// estado do jogo 2

			}
		}
		if (beteumoedaOuro) {
			if (estadojogo == 1) {
				pontos += 10;
				moedapravalor = 0;
				somMoedas.play();
				posicaoMOedaouro = larguradispositivo;

			}
		}
		if (beteumoedaPrata) {

			if (estadojogo == 1) {
				pontos += 5;
				moedapravalor++;
				somMoedas.play();
				posicaoMOedaPrata = larguradispositivo;

			}
		}
	}

	private void validarPontos() {
		if (posicaoCanoHorizontal < 50 - passaros.getWidth()) {// se passar pelo cano
			if (!passouCano) {// se for diferente passouCano
				pontos++; // soma os pontos
				passouCano = true;// se for verdadeiro
				somPontuacao.play();// vai disparar o som de pontuação


			}

		}

		variacao += Gdx.graphics.getDeltaTime() * 10;// velocidade da variação de imagens do passaro

		if (variacao > 3) // variação para animação do passaro
		{
			variacao = 0; // determina que sera 0
		}
	}

	private void verificaEstadojogo() {

		boolean toqueTela = Gdx.input.justTouched();// bool que verifica o toque
		// estado 0, se tocar na tela, a gravidade do passaro ô puxa pra baixo, e muda estado do jogo para 1 e dispara o som de voando
		if (estadojogo == 0) {
			if (Gdx.input.justTouched()) {
				gravidade = -15;
				estadojogo = 1;
				somVoando.play();
			}

		}
		// estado do jogo 1, que ativa a gravidade e dispara o som de voando, e faz que o cano comece a se movimentar,
		else if (estadojogo == 1) {
			valor = 0;
			if (toqueTela) {
				gravidade = -15;
				somVoando.play();
			}
			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;// velocidade do cano
			if (posicaoCanoHorizontal < -canoBaixo.getWidth()) {
				posicaoCanoHorizontal = larguradispositivo; // a posição do cano na horizontal é igual a largura
				posicaoCanoVertical = random.nextInt(400) - 200;// mura randomicamente
				passouCano = false;
			}
			posicaoMOedaPrata -= Gdx.graphics.getDeltaTime() * 150;
			if (posicaoMOedaPrata < -moedaPrata.getWidth()) {
				posicaoMOedaPrata = larguradispositivo;
				posicaomoedavetical = random.nextInt(300) - 200;

			}
			if (moedapravalor >= 5) {
				posicaoMOedaouro -= Gdx.graphics.getDeltaTime() * 150;
				if (posicaoMOedaouro < -moedaOuro.getWidth()) {
					posicaoMOedaouro = larguradispositivo;
					posicaomoedavetical = random.nextInt(300) - 200;
					moedapravalor = 0;
				}
			}

			if (posicaoInicialVerticalPassaro > 0 || toqueTela)
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;


			gravidade++;// aumento de gravidade


		}
		// estado do jogo 2, que faz que se colidir com os canos, mostre a melhor pontuação e resete o game, caso tocar na tela novamente
		else if (estadojogo == 2) {
			if (pontos > pontuacaoMaxima) // se pontuação  for maior que pontuação maxima, potuanção maxima sera igual a pontos
			{
				pontuacaoMaxima = pontos;
				preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);
			}

			posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime() * 500; // efeito de colisão quando acontecer

			if (toqueTela) // se tocou na tela, reseta o estado do jogo como pontuação,, entre outros
			{
				estadojogo = 0;// o estado do jogo
				pontos = 0;// os pontos
				gravidade = 0;// a gravidade
				posicaoHorizontalPassaro = 0;
				posicaoInicialVerticalPassaro = alturadispositivo / 2;
				posicaoCanoHorizontal = larguradispositivo;
				posicaoMOedaouro = larguradispositivo;
				posicaoMOedaPrata = larguradispositivo;
				moedapravalor = 0;
			}
		}


	}

	private void desenharTexturas() {
		batch.begin();// inicio

		batch.draw(fundo, 0, 0, larguradispositivo, alturadispositivo);// rederiza o fundo do game na cena

		batch.draw(passaros, 50 + posicaoHorizontalPassaro, posicaoInicialVerticalPassaro);// rederiza o passaro na cena
		batch.draw(canoBaixo, posicaoCanoHorizontal, alturadispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical);//renderiza o cano na cena e calcula conforme o tamanho da tela
		batch.draw(canoAlto, posicaoCanoHorizontal, alturadispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical);//renderiza o cano na cena e calcula conforme o tamanho da tela
		textPontuacao.draw(batch, String.valueOf(pontos), larguradispositivo / 2, alturadispositivo - 100);// renderiza os pontos na tela cada vez que passar entre os canos

		// se o estado do jogo for 2, ele renderiza na tela a imagens, dando infomação sobre o detalhes no qual o jogo se encotra
		if (estadojogo == 2) {
			batch.draw(GameOver, larguradispositivo / 2 - GameOver.getWidth() / 2f, alturadispositivo / 2);
			textRenicia.draw(batch, "Toque  na tela para reiniciar!", larguradispositivo / 2 - 200, alturadispositivo / 2 - GameOver.getHeight() / 2f);
			textMelhorPontuacao.draw(batch, "Sua melhor pontuação  é : " + pontuacaoMaxima + " Pontos", larguradispositivo / 2 - 300, alturadispositivo / 2 - GameOver.getHeight() * 2);
		}
		if (estadojogo == 0 && valor == 1 )
		{
			batch.draw(logo,posicaoHorizontalPassaro,alturadispositivo /4,1200,800);

		}
		if (moedapravalor <= 5) {

			batch.draw(moedaPrata, posicaoMOedaPrata, alturadispositivo /2 + posicaomoedavetical + moedaPrata.getHeight() / 2f);
		}

		if (moedapravalor >= 5) {

			batch.draw(moedaOuro, posicaoMOedaouro, alturadispositivo /2 + posicaomoedavetical + moedaOuro.getHeight() / 2f);


		}

		batch.end();// fim!

	}

	@Override
	public void dispose()
	{
	}
}


