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
	//tudo para guardar as texturas
	private Texture passaros;
	private Texture fundo;
	private Texture canoAlto;
	private Texture canoBaixo;
	private Texture GameOver;
	private Texture moedaPrata;
	private Texture moedaOuro;
	private Texture logo;


	BitmapFont textPontuacao;// mostra o texto dos pontos
	BitmapFont textRenicia; // mostra o reiniciar jogo
	BitmapFont textMelhorPontuacao;// mostra a melhor pontuação

	private boolean passouCano = false;// verificação de se é verdadeiro ou falso quando passar no cano

	private Random random;// vairavel de random

	private int pontuacaoMaxima = 0;
	private int pontos = 0;// variavel de pontos
	private int gravidade = 0;// variavel de gravidade
	private int estadojogo = 0;// variavel de estado do jogo
	private int moedapravalor = 0;
	int valor = 1;

	private float variacao = 0; // animações
	private float posicaoInicialVerticalPassaro = 0;// passaro na vertical
	private float posicaoCanoHorizontal; // cano na horizontal
	private float posicaoCanoVertical; // cano na vertical
	private float larguradispositivo;//  largura do dispositivo
	private float alturadispositivo;//  altura do dispositivo
	private float espacoEntreCanos;// espaçamento dos canos
	private float posicaoHorizontalPassaro = 0;
	private float posicaoMOedaouro;
	private float posicaoMOedaPrata;
	private float posicaomoedavetical;


	private ShapeRenderer shapeRenderer;
	//  variaveis de colisão
	private Circle circuloPassaro;
	private Rectangle retaguloCanoCima;
	private Rectangle retanguloBaixo;
	private Circle ciculoMoedaOuro;
	private Circle ciculoMoedaPrata;

	// sonzin do game
	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;
	Sound somMoedas;

	Preferences preferencias;



	@Override
	public void create() {
		inicializaTexuras();// inicializando as texturas
		inicializarObjetos();// inicializando os objetos


	}

	private void inicializarObjetos() {

		batch = new SpriteBatch();
		random = new Random(); // o random


		alturadispositivo = Gdx.graphics.getHeight();//  altura é a mesma do dispositivo
		larguradispositivo = Gdx.graphics.getWidth();//  largura é a mesma do dispositivo
		posicaoInicialVerticalPassaro = alturadispositivo / 2;// serve para pegar a metade
		posicaomoedavetical = alturadispositivo / 2;
		posicaoCanoHorizontal = larguradispositivo;// a posição do cano será a mesma da largura do dispositivo
		posicaoMOedaouro = larguradispositivo;
		posicaoMOedaPrata = larguradispositivo;

		espacoEntreCanos = 350;


		textPontuacao = new BitmapFont();// texto dos pontos
		textPontuacao.setColor(Color.WHITE);// mudando para cor branca
		textRenicia = new BitmapFont();//  texto de pontos
		textPontuacao.getData().setScale(10);//  tamanho do texto

		textRenicia.setColor(Color.GREEN);// mudando para cor branca
		textRenicia.getData().setScale(3);// tamanho do texto

		textMelhorPontuacao = new BitmapFont();//  texto dos pontos
		textMelhorPontuacao.setColor(Color.RED);// mudando para cor branca
		textMelhorPontuacao.getData().setScale(3);// tamanho do texto

		// colisões
		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retaguloCanoCima = new Rectangle();
		retanguloBaixo = new Rectangle();
		ciculoMoedaOuro = new Circle();
		ciculoMoedaPrata = new Circle();

		// audios
		somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));
		somMoedas = Gdx.audio.newSound(Gdx.files.internal("Som_Moeda.mp3"));


		preferencias = Gdx.app.getPreferences("flappyBird");
		pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);// guardar a maior pontuação

	}

	private void inicializaTexuras() {

		fundo = new Texture("fundo.png");// textura para criação

		// colocando as imagens do passaro em um array para que forme a animação
		passaros = new Texture("Bomba.png");
		// texturas do cano
		canoAlto = new Texture("cano_topo_maior.png");
		canoBaixo = new Texture("cano_baixo_maior.png");

		moedaOuro = new Texture("MoedaOuro.png");
		moedaPrata = new Texture("MoedaPrata1.png");

		GameOver = new Texture("game_over.png");
		logo = new Texture("LogoFIm.png");


	}

	@Override
	public void render() {

		verificaEstadojogo();// verificar os estados do jogo
		desenharTexturas();// renderizar as texuras
		detectarColisao();// detectar as colisões dos objetos
		validarPontos();// validar os pontos quando passar


	}

	private void detectarColisao() {

		circuloPassaro.set(50 + passaros.getWidth() / 2f,
				posicaoInicialVerticalPassaro + passaros.getHeight() / 2f,
				passaros.getWidth() / 2f);// colisão do bird

		retanguloBaixo.set(posicaoCanoHorizontal, alturadispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical, canoBaixo.getWidth(), canoBaixo.getHeight());// colisão dos canos

		retaguloCanoCima.set(posicaoCanoHorizontal, alturadispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical, canoAlto.getWidth(), canoAlto.getHeight());// colisão dos canos

		ciculoMoedaPrata.set(posicaoMOedaPrata, alturadispositivo /2 + posicaomoedavetical + moedaPrata.getHeight() / 2f,
				moedaPrata.getWidth() / 2f);

		ciculoMoedaOuro.set(posicaoMOedaouro, alturadispositivo /2 + posicaomoedavetical + moedaOuro.getHeight() / 2f,
				moedaOuro.getWidth() / 2f);

		boolean beteumoedaOuro = Intersector.overlaps(circuloPassaro, ciculoMoedaOuro);
		boolean beteumoedaPrata = Intersector.overlaps(circuloPassaro, ciculoMoedaPrata);
		boolean bateuCanoCima = Intersector.overlaps(circuloPassaro, retaguloCanoCima);// verifica a colisão do bird e dos canos
		boolean bateuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloBaixo);// verifica a colisão do bird e dos canos
		if (bateuCanoBaixo || bateuCanoCima) {
			// se o estado for 1, vai disparar o som da colisão e mudar o estado do jogo para 2
			if (estadojogo == 1) {
				somColisao.play();// dispara o som da colisão
				estadojogo = 2;// o estado do jogo

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
		if (posicaoCanoHorizontal < 50 - passaros.getWidth()) {// quando passar pelo cano
			if (!passouCano) {// se for diferente de passou cano
				pontos++; // vai somar os pontos
				passouCano = true;//  passouCano é verdadeiro
				somPontuacao.play();// aparece o som de pontuação


			}

		}

		variacao += Gdx.graphics.getDeltaTime() * 10;// velocidade da variação das imagens para a animação

		if (variacao > 3) // mudança animação do bird
		{
			variacao = 0; // determina que será valor 0
		}
	}

	private void verificaEstadojogo() {

		boolean toqueTela = Gdx.input.justTouched();// bool de toque
		// estado 0, ao tocar na tela, a  gravidade do bird puxa pra baixo, e muda estado do jogo para 1, disparando um som
		if (estadojogo == 0) {
			if (Gdx.input.justTouched()) {
				gravidade = -15;
				estadojogo = 1;
				somVoando.play();
			}

		}
		// estado do jogo de numero 1, ele ativa a gravidade e dispara o som de voando, e faz o cano começar a se movimentar,
		else if (estadojogo == 1) {
			valor = 0;
			if (toqueTela) {
				gravidade = -15;
				somVoando.play();
			}
			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;// velocidade do cano
			if (posicaoCanoHorizontal < -canoBaixo.getWidth()) {
				posicaoCanoHorizontal = larguradispositivo; //a posição cano na horizontal é igual a largura dele
				posicaoCanoVertical = random.nextInt(400) - 200;//a posição vertical muda randomicamente
				passouCano = false;
			}
			posicaoMOedaPrata -= Gdx.graphics.getDeltaTime() * 150;
			if (posicaoMOedaPrata < -moedaPrata.getWidth()) {
				posicaoMOedaPrata = larguradispositivo;
				posicaomoedavetical = random.nextInt(300) - 200;

			}
			if (moedapravalor >= 10) {
				posicaoMOedaouro -= Gdx.graphics.getDeltaTime() * 150;
				if (posicaoMOedaouro < -moedaOuro.getWidth()) {
					posicaoMOedaouro = larguradispositivo;
					posicaomoedavetical = random.nextInt(300) - 200;
					moedapravalor = 0;
				}
			}

			if (posicaoInicialVerticalPassaro > 0 || toqueTela)
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;


			gravidade++;// aumenta a gravidade


		}
		// segundo estado do jogo, de numero 2, que quando colidir com os canos, motrar a melhor pontuação e resetar o game ao tocar na tela
		else if (estadojogo == 2) {
			if (pontos > pontuacaoMaxima) // se pontuação  for  maior  que pontuação maxima, ela vai ser igual os pontos
				pontuacaoMaxima = pontos;
				preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);
			}

			posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime() * 500; // é o efeito da colisão quando colidir

			if (toqueTela) // ao tocar na tela, vai resetar o estado do jogo, sendo a pontuação, a posição do passaro e os canos
			{
				estadojogo = 0;// estado do jogo
				pontos = 0;// pontos
				gravidade = 0;// gravidade
				posicaoHorizontalPassaro = 0;
				posicaoInicialVerticalPassaro = alturadispositivo / 2;
				posicaoCanoHorizontal = larguradispositivo;
				posicaoMOedaouro = larguradispositivo;
				posicaoMOedaPrata = larguradispositivo;
				moedapravalor = 0;
			}
		}



	private void desenharTexturas() {
		batch.begin();// iniciando

		batch.draw(fundo, 0, 0, larguradispositivo, alturadispositivo);// renderiza o fundo do game
		if (estadojogo == 0 && valor == 1 )
		{
			batch.draw(logo,posicaoHorizontalPassaro,alturadispositivo /4,1200,800);

		}
		batch.draw(passaros, 50 + posicaoHorizontalPassaro, posicaoInicialVerticalPassaro);// renderiza o bird na cena
		batch.draw(canoBaixo, posicaoCanoHorizontal, alturadispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical);//renderiza o cano na cena e calcula conforme o tamanho da tela
		batch.draw(canoAlto, posicaoCanoHorizontal, alturadispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical);//renderiza o cano na cena e calcula conforme o tamanho da tela
		textPontuacao.draw(batch, String.valueOf(pontos), larguradispositivo / 2, alturadispositivo - 100);// renderiza os pontos na tela e cada vez que passar entre os canos

		// se o estado do jogo for 2, ele renderiza na tela a imagens dando infomação sobre como o jogo se encotra
		if (estadojogo == 2) {
			batch.draw(GameOver, larguradispositivo / 2 - GameOver.getWidth() / 2f, alturadispositivo / 2);
			textRenicia.draw(batch, "Toque  na tela para reiniciar!", larguradispositivo / 2 - 200, alturadispositivo / 2 - GameOver.getHeight() / 2f);
			textMelhorPontuacao.draw(batch, "Sua melhor pontuação  é : " + pontuacaoMaxima + " Pontos", larguradispositivo / 2 - 300, alturadispositivo / 2 - GameOver.getHeight() * 2);
		}
		if (moedapravalor <= 10) {

			batch.draw(moedaPrata, posicaoMOedaPrata, alturadispositivo /2 + posicaomoedavetical + moedaPrata.getHeight() / 2f);
		}

		if (moedapravalor >= 10) {

			batch.draw(moedaOuro, posicaoMOedaouro, alturadispositivo /2 + posicaomoedavetical + moedaOuro.getHeight() / 2f);


		}

		batch.end();// fim!

	}

	@Override
	public void dispose() {


	}


}