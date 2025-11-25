import React, { useState, useEffect, useCallback, createContext, useContext } from 'react';
import { Loader2, Zap, LayoutDashboard, User, CheckCircle, XCircle, FileText, Calendar, Send, Save, ArrowLeft, ClipboardList, AlertTriangle, LogIn, UserPlus, LogOut, ShieldCheck, Home } from 'lucide-react';

/* ===============================================
 * 1. CONFIGURATION API & CONTEXTE D'AUTH
 * =============================================== */

// URLs de base pour les deux contrôleurs
const API_BASE_URL = 'http://localhost:8080/api/v1/requests';
const AUTH_BASE_URL = 'http://localhost:8080/api/auth';

// Création d'un Contexte React pour gérer l'état de l'authentification
const AuthContext = createContext(null);

/**
 * Hook personnalisé pour accéder facilement aux données d'authentification
 * (isLoggedIn, user, token, login, logout)
 */
const useAuth = () => {
  return useContext(AuthContext);
};

/**
 * Provider d'Authentification
 * Ce composant enveloppe l'application et gère l'état de connexion.
 */
const AuthProvider = ({ children }) => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [jwtToken, setJwtToken] = useState(null);
  const [userRoles, setUserRoles] = useState([]);
  const [username, setUsername] = useState("");
  const [authIsLoading, setAuthIsLoading] = useState(false);
  const [authError, setAuthError] = useState(null);

  // Fonction de Connexion (Login)
  const login = async (username, password) => {
    setAuthIsLoading(true);
    setAuthError(null);
    try {
      // 1. Appel à l'API de login (backend Spring)
      const response = await fetch(`${AUTH_BASE_URL}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Échec de la connexion');
      }

      const data = await response.json();
      
      // 2. Stockage des informations de connexion
      setJwtToken(data.token);
      setUserRoles(data.roles || []);
      setUsername(data.username);
      setIsLoggedIn(true);

      return true; // Succès

    } catch (err) {
      setAuthError(err.message);
      setIsLoggedIn(false);
      return false; // Échec
    } finally {
      setAuthIsLoading(false);
    }
  };
  
  // Fonction d'Inscription (Signup)
  const register = async (username, password) => {
    setAuthIsLoading(true);
    setAuthError(null);
    try {
      const response = await fetch(`${AUTH_BASE_URL}/signup`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || 'Échec de l\'inscription');
      }
      
      // Après l'inscription, on tente une connexion automatique
      return await login(username, password);

    } catch (err) {
      setAuthError(err.message);
      setIsLoggedIn(false);
      return false; // Échec
    } finally {
      setAuthIsLoading(false);
    }
  };

  // Fonction de Déconnexion
  const logout = () => {
    setJwtToken(null);
    setUserRoles([]);
    setUsername("");
    setIsLoggedIn(false);
  };

  // Valeur fournie au reste de l'application
  const value = {
    isLoggedIn,
    jwtToken,
    userRoles,
    username,
    authIsLoading,
    authError,
    login,
    register,
    logout,
    isAdmin: userRoles.includes('ROLE_ADMIN') // Helper pour vérifier si l'utilisateur est admin
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};


/**
 * Fonction générique pour les appels API SÉCURISÉS (ajoute le jeton JWT)
 */
const apiCall = async (endpoint, method = 'GET', data = null, token) => {
  const url = `${API_BASE_URL}${endpoint}`;
  
  const headers = {
    'Content-Type': 'application/json',
  };

  // Ajoute le jeton JWT à l'en-tête Authorization s'il est fourni
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const options = {
    method,
    headers,
  };

  if (data) {
    options.body = JSON.stringify(data);
  }

  const controller = new AbortController();
  options.signal = controller.signal;
  const timeoutId = setTimeout(() => controller.abort(), 15000);

  try {
    const response = await fetch(url, options);
    clearTimeout(timeoutId);

    if (response.ok) {
      const contentType = response.headers.get("content-type");
      if (contentType && contentType.includes("application/json")) {
        return await response.json();
      }
      return null;
    } else {
      let errorDetail = "Erreur du serveur (Statut: " + response.status + ")";
      try {
        const errorBody = await response.json();
        // Utilisation du message d'erreur de notre RestExceptionHandler
        errorDetail = errorBody.error || errorBody.message || errorDetail;
      } catch (e) {
         // Le corps n'est pas du JSON
      }
      if (response.status === 401 || response.status === 403) {
         throw new Error(`[Erreur ${response.status}] Accès Non Autorisé. Vérifiez votre jeton.`);
      }
      throw new Error(`[API Error ${response.status}] ${errorDetail}`);
    }
  } catch (error) {
    clearTimeout(timeoutId);
    if (error.name === 'AbortError') {
      throw new Error("Timeout: La requête a expiré après 15s.");
    }
    throw new Error("Erreur de Communication avec le Serveur (Failed to fetch). Vérifiez que le backend Spring Boot est démarré sur http://localhost:8080 et que le CORS est configuré.");
  }
};

/**
 * Composant pour afficher les messages de feedback (Erreur/Succès/Info).
 */
const FeedbackMessage = ({ type, text }) => {
  let classes, Icon;
  if (type === 'error') {
    classes = "bg-red-100 border-red-400 text-red-700";
    Icon = XCircle;
  } else if (type === 'success') {
    classes = "bg-yellow-100 border-yellow-400 text-yellow-800";
    Icon = Save;
  } else if (type === 'submit-success') {
    classes = "bg-green-100 border-green-400 text-green-800";
    Icon = CheckCircle;
  } else if (type === 'info') {
    classes = "bg-blue-100 border-blue-400 text-blue-700";
    Icon = AlertTriangle;
  }

  return (
    <div className={`flex items-start p-4 rounded-xl shadow-lg border-2 ${classes} my-4 transition-all duration-500`}>
      <Icon className="w-5 h-5 mt-1 flex-shrink-0" />
      <p className="ml-3 font-medium">{text}</p>
    </div>
  );
};

/* ===============================================
 * 2. COMPOSANTS DE VUE (Admin Dashboard)
 * =============================================== */

const AdminActionButton = ({ onClick, label, icon: Icon, colorClass, disabled }) => (
  <button
    onClick={onClick}
    disabled={disabled}
    className={`flex items-center space-x-2 px-3 py-1.5 rounded-lg text-sm font-semibold transition-all duration-300 shadow-md ${colorClass} disabled:opacity-50 disabled:cursor-not-allowed transform hover:scale-[1.02]`}
  >
    {Icon && <Icon className="w-4 h-4" />}
    <span>{label}</span>
  </button>
);

const RequestRow = ({ request, onUpdate, isUpdating, token }) => {
  const handleAction = async (action) => {
    if (!window.confirm(`Êtes-vous sûr de vouloir ${action} la demande ${request.id}?`)) {
      return;
    }
    onUpdate(request.id, 'start');
    try {
      await apiCall(`/${request.id}/${action}`, 'PUT', null, token); // Passe le jeton
      onUpdate(request.id, 'success');
    } catch (error) {
      onUpdate(request.id, 'error', error.message);
    }
  };

  const isLoading = isUpdating === request.id;

  return (
    <tr className="bg-white border-b hover:bg-gray-50 transition-colors">
      <td className="px-6 py-3 font-medium text-gray-900">{request.id}</td>
      <td className="px-6 py-3 text-gray-700">{request.documentType}</td>
      <td className="px-6 py-3 text-gray-700">{request.title}</td>
      <td className="px-6 py-3 text-gray-700">{request.birthDate}</td>
      <td className="px-6 py-3 text-gray-700">{request.userId}</td>
      <td className="px-6 py-3 text-sm font-medium whitespace-nowrap space-x-2">
        <AdminActionButton
          label={isLoading ? 'Chargement...' : 'Approuver'}
          icon={CheckCircle}
          colorClass="bg-green-600 text-white hover:bg-green-700"
          onClick={() => handleAction('approve')}
          disabled={isLoading}
        />
        <AdminActionButton
          label={isLoading ? 'Chargement...' : 'Rejeter'}
          icon={XCircle}
          colorClass="bg-red-600 text-white hover:bg-red-700"
          onClick={() => handleAction('reject')}
          disabled={isLoading}
        />
      </td>
    </tr>
  );
};

const AdminDashboardView = () => {
  const [requests, setRequests] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [globalError, setGlobalError] = useState(null);
  const [isUpdating, setIsUpdating] = useState(null);
  const [message, setMessage] = useState(null);
  const { jwtToken, logout } = useAuth(); // Récupère le jeton et la fonction logout

  const fetchRequests = useCallback(async () => {
    setIsLoading(true);
    setGlobalError(null);
    try {
      const data = await apiCall('/status/SUBMITTED', 'GET', null, jwtToken); // Passe le jeton
      setRequests(data || []);
    } catch (err) {
      setGlobalError(err.message);
      if (err.message.includes("401") || err.message.includes("403")) {
        logout(); // Si le jeton est invalide ou expiré, déconnexion
      }
    } finally {
      setIsLoading(false);
    }
  }, [jwtToken, logout]);

  useEffect(() => {
    fetchRequests();
  }, [fetchRequests]);

  const handleUpdate = (requestId, type, errorMessage = '') => {
    if (type === 'start') {
      setIsUpdating(requestId);
      return;
    }
    setIsUpdating(null);
    if (type === 'success') {
      setMessage({ type: 'success', text: `Demande ${requestId} mise à jour.` });
      setRequests(prev => prev.filter(req => req.id !== requestId));
    } else {
      setMessage({ type: 'error', text: `Erreur ${requestId}: ${errorMessage}` });
    }
  };

  const renderContent = () => {
    if (isLoading) {
      return (
        <div className="flex justify-center items-center py-10">
          <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
        </div>
      );
    }
    if (globalError) {
      return (
        <div className="text-center py-10 bg-red-50 rounded-xl border border-dashed border-red-300">
          <XCircle className="w-10 h-10 mx-auto text-red-500" />
          <p className="mt-2 text-xl font-semibold text-red-700">Échec du chargement</p>
          <p className="mt-1 text-red-500 font-mono text-sm">{globalError}</p>
          <button onClick={fetchRequests} className="mt-4 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition">
            Réessayer
          </button>
        </div>
      );
    }
    if (requests.length === 0) {
      return (
        <div className="text-center py-10 bg-gray-50 rounded-xl border border-dashed border-gray-300">
          <ClipboardList className="w-10 h-10 mx-auto text-gray-500" />
          <p className="mt-2 text-xl font-semibold text-gray-700">Aucune demande en attente</p>
        </div>
      );
    }
    return (
      <div className="overflow-x-auto shadow-xl rounded-xl">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-blue-600 text-white">
            <tr>
              {['ID', 'Type Document', 'Motif', 'Date Naissance', 'ID Utilisateur', 'Actions'].map(header => (
                <th key={header} className="px-6 py-3 text-left text-xs font-bold uppercase tracking-wider">
                  {header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {requests.map(request => (
              <RequestRow
                key={request.id}
                request={request}
                onUpdate={handleUpdate}
                isUpdating={isUpdating}
                token={jwtToken} // Passe le jeton à la ligne
              />
            ))}
          </tbody>
        </table>
      </div>
    );
  };

  return (
    <div className="space-y-6 p-6 bg-white rounded-xl shadow-2xl">
      <header className="flex items-center justify-between border-b pb-4">
        <div className="flex items-center space-x-3">
          <LayoutDashboard className="w-7 h-7 text-blue-800" />
          <h2 className="text-2xl font-extrabold text-gray-900">Tableau de Bord Administrateur</h2>
        </div>
        <AdminActionButton
            label="Actualiser"
            icon={Zap}
            colorClass="bg-blue-500 text-white hover:bg-blue-600"
            onClick={fetchRequests}
            disabled={isLoading}
        />
      </header>
      {message && (
        <FeedbackMessage type={message.type} text={message.text} />
      )}
      {renderContent()}
    </div>
  );
};

/* ===============================================
 * 4. COMPOSANTS DE VUE (User Form)
 * =============================================== */

const UserFormView = () => {
  const { username, jwtToken } = useAuth(); // Récupère le nom d'utilisateur et le jeton

  const initialFormState = {
    title: "Renouvellement",
    documentType: "CARTE IDENTITE",
    birthDate: "",
    // Utilise le nom d'utilisateur de la session (ou un ID si vous le préférez)
    userId: username, 
    status: "DRAFT"
  };

  const [formData, setFormData] = useState(initialFormState);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [draftId, setDraftId] = useState(null);
  const [message, setMessage] = useState(null);

  const documentOptions = ["CARTE IDENTITE", "PASSEPORT", "PERMIS DE CONDUIRE"];
  const motifOptions = ["Renouvellement", "Perte", "Vol", "Première Demande"];

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setError(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);
    setDraftId(null);
    setMessage(null);

    if (!formData.birthDate) {
      setError("Veuillez remplir tous les champs.");
      setIsLoading(false);
      return;
    }

    try {
      const response = await apiCall('', 'POST', formData, jwtToken); // Passe le jeton

      if (response && response.id) {
        setDraftId(response.id);
        setMessage({
          type: 'success',
          text: `Brouillon créé (ID: ${response.id}). Soumettez-le pour validation.`
        });
      } else {
        throw new Error("Réponse de création invalide.");
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleDraftSubmission = async () => {
    if (!draftId) return;
    if (!window.confirm(`Soumettre la demande ID ${draftId} pour validation?`)) return;

    setIsLoading(true);
    setError(null);
    setMessage(null);

    try {
      await apiCall(`/${draftId}/submit`, 'PUT', null, jwtToken); // Passe le jeton

      setMessage({
        type: 'submit-success',
        text: `Demande ID ${draftId} soumise avec succès!`
      });
      setDraftId(null);
      setFormData(initialFormState); // Réinitialiser
    } catch (err) {
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleNewForm = () => {
    setFormData(initialFormState);
    setDraftId(null);
    setMessage(null);
    setError(null);
  };
  
  const InputField = ({ label, id, ...props }) => (
    <div>
      <label htmlFor={id} className="block text-sm font-medium text-gray-700 mb-1">
        {label}
      </label>
      <input 
        id={id} 
        name={id}
        className="w-full px-4 py-2 border border-gray-300 rounded-xl shadow-sm focus:ring-blue-500 focus:border-blue-500 transition duration-150"
        {...props}
      />
    </div>
  );
  
  const SelectField = ({ label, id, value, onChange, options, ...props }) => (
     <div>
      <label htmlFor={id} className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
      <select
        id={id}
        name={id}
        value={value}
        onChange={onChange}
        required
        className="w-full px-4 py-2 border border-gray-300 rounded-xl shadow-sm focus:ring-blue-500 focus:border-blue-500 transition duration-150"
        {...props}
      >
        {options.map(opt => <option key={opt} value={opt}>{opt}</option>)}
      </select>
    </div>
  );


  return (
    <div className="max-w-xl mx-auto p-6 bg-white rounded-xl shadow-2xl border border-blue-100">
      <header className="flex items-center space-x-3 border-b pb-4 mb-6">
        <FileText className="w-7 h-7 text-blue-800" />
        <h2 className="text-2xl font-extrabold text-gray-900">Formulaire de Demande Officielle</h2>
      </header>
      
      <p className="mb-4 text-gray-600 border-l-4 border-blue-400 pl-3 py-1 bg-blue-50">
        Créez un nouveau brouillon. Votre demande sera soumise après la création.
      </p>

      {(error || message) && <FeedbackMessage type={error ? 'error' : message.type} text={error || message.text} />}

      {message && message.type === 'submit-success' ? (
        <div className="text-center py-10">
          <CheckCircle className="w-16 h-16 mx-auto text-green-600" />
          <h3 className="mt-4 text-xl font-bold text-gray-900">Demande Soumise !</h3>
          <p className="mt-2 text-gray-600">Votre demande est en attente d'approbation.</p>
          <button
            onClick={handleNewForm}
            className="mt-6 flex items-center justify-center mx-auto space-x-2 px-6 py-3 rounded-xl text-white font-bold bg-blue-600 hover:bg-blue-700 shadow-lg transform hover:scale-[1.02] transition duration-300"
          >
            <ArrowLeft className="w-5 h-5" />
            <span>Nouvelle Demande</span>
          </button>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="space-y-6">
          <SelectField 
            label="Document Requis"
            id="documentType"
            value={formData.documentType}
            onChange={handleChange}
            options={documentOptions}
            disabled={draftId}
          />
          <SelectField 
            label="Motif de la Demande"
            id="title"
            value={formData.title}
            onChange={handleChange}
            options={motifOptions}
            disabled={draftId}
          />
          <InputField
            label="Date de Naissance (YYYY-MM-DD)"
            id="birthDate"
            type="date"
            value={formData.birthDate}
            onChange={handleChange}
            required
            disabled={draftId}
          />
          <InputField
            label="Votre ID Utilisateur (pour le suivi)"
            id="userId"
            type="text"
            value={formData.userId}
            disabled
            className="w-full px-4 py-2 bg-gray-50 border border-gray-300 rounded-xl shadow-inner text-gray-500 cursor-not-allowed"
          />

          {!draftId ? (
            <button
              type="submit"
              disabled={isLoading}
              className="w-full flex items-center justify-center space-x-3 px-6 py-3 rounded-xl text-white font-bold bg-gray-500 hover:bg-gray-600 shadow-lg transform hover:scale-[1.01] transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isLoading ? <Loader2 className="w-5 h-5 animate-spin" /> : <Save className="w-5 h-5" />}
              <span>{isLoading ? 'Sauvegarde...' : '1. Sauvegarder (Brouillon)'}</span>
            </button>
          ) : (
            <button
              type="button"
              onClick={handleDraftSubmission}
              disabled={isLoading}
              className="w-full flex items-center justify-center space-x-3 px-6 py-3 rounded-xl text-white font-bold bg-blue-600 hover:bg-blue-700 shadow-xl shadow-blue-300 transform hover:scale-[1.01] transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isLoading ? <Loader2 className="w-5 h-5 animate-spin" /> : <Send className="w-5 h-5" />}
              <span>{isLoading ? 'Soumission...' : '2. Soumettre la Demande'}</span>
            </button>
          )}
        </form>
      )}
    </div>
  );
};

/* ===============================================
 * 5. COMPOSANTS DE VUE (Login & Register)
 * =============================================== */

/**
 * Vue pour le Login et l'Inscription
 */
const AuthView = () => {
  const [isLoginView, setIsLoginView] = useState(true);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const { login, register, authIsLoading, authError } = useAuth();
  const [localMessage, setLocalMessage] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLocalMessage(null);
    let success = false;
    
    if (isLoginView) {
      success = await login(username, password);
    } else {
      success = await register(username, password);
      if (success) {
        setLocalMessage("Inscription réussie ! Connexion...");
      }
    }
  };

  const InputField = ({ label, id, type, value, onChange }) => (
    <div>
      <label htmlFor={id} className="block text-sm font-medium text-gray-700">
        {label}
      </label>
      <input
        id={id}
        name={id}
        type={type}
        value={value}
        onChange={onChange}
        required
        className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
      />
    </div>
  );

  return (
    <div className="flex items-center justify-center min-h-[70vh] bg-gray-100">
      <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-2xl shadow-2xl border border-gray-200">
        <h2 className="text-3xl font-extrabold text-center text-gray-900">
          {isLoginView ? "Connexion" : "Créer un Compte"}
        </h2>
        
        {authError && <FeedbackMessage type="error" text={authError} />}
        {localMessage && <FeedbackMessage type="submit-success" text={localMessage} />}

        <form className="space-y-6" onSubmit={handleSubmit}>
          <InputField 
            label="Nom d'utilisateur"
            id="username"
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <InputField 
            label="Mot de passe"
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          
          <button
            type="submit"
            disabled={authIsLoading}
            className="w-full flex justify-center items-center space-x-2 px-6 py-3 border border-transparent rounded-xl shadow-lg text-white font-bold bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition duration-300 transform hover:scale-[1.01] disabled:opacity-50"
          >
            {authIsLoading ? (
              <Loader2 className="w-5 h-5 animate-spin" />
            ) : (
              isLoginView ? <LogIn className="w-5 h-5" /> : <UserPlus className="w-5 h-5" />
            )}
            <span>{authIsLoading ? "Chargement..." : (isLoginView ? "Se Connecter" : "S'inscrire")}</span>
          </button>
        </form>
        
        <p className="text-center text-sm text-gray-600">
          {isLoginView ? "Pas encore de compte ?" : "Déjà un compte ?"}
          <button
            onClick={() => {
              setIsLoginView(!isLoginView);
              setAuthError(null);
            }}
            className="ml-1 font-medium text-blue-600 hover:text-blue-500"
          >
            {isLoginView ? "S'inscrire" : "Se Connecter"}
          </button>
        </p>
      </div>
    </div>
  );
};


/* ===============================================
 * 6. COMPOSANT PRINCIPAL (App)
 * =============================================== */

const AppLayout = ({ children }) => {
  const { isLoggedIn, username, isAdmin, logout } = useAuth();

  const Logo = () => (
    <div className="flex items-center space-x-2">
      <ShieldCheck className="w-7 h-7 text-white" />
      <span className="text-xl font-extrabold text-white tracking-wider">E-SERVICE (Secure)</span>
    </div>
  );

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col font-inter">
      {/* Header */}
      <header className="bg-blue-800 shadow-2xl p-4 sticky top-0 z-10">
        <div className="max-w-7xl mx-auto flex items-center justify-between">
          <Logo />
          {isLoggedIn && (
            <div className="flex items-center space-x-4">
              <span className="text-blue-200 text-sm">
                Connecté: <strong className="text-white font-bold">{username}</strong> {isAdmin && "(Admin)"}
              </span>
              <button
                onClick={logout}
                className="flex items-center space-x-2 px-3 py-1.5 rounded-lg text-sm font-semibold transition-all duration-300 shadow-md bg-red-600 text-white hover:bg-red-700"
              >
                <LogOut className="w-4 h-4" />
                <span>Déconnexion</span>
              </button>
            </div>
          )}
        </div>
      </header>

      {/* Contenu principal */}
      <main className="flex-grow p-4 sm:p-8">
        <div className="max-w-7xl mx-auto">
          {children}
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-gray-800 text-white text-center p-4">
        <p className="text-sm opacity-70">
          &copy; 2025 E-Service Platform (JWT Edition) - Démonstration technique.
        </p>
      </footer>
    </div>
  );
};

/**
 * Composant Racine de l'Application
 * Gère l'affichage (Login ou Contenu Principal)
 */
const RootApp = () => {
  const { isLoggedIn, isAdmin } = useAuth();
  const [currentTab, setCurrentTab] = useState(isAdmin ? 'ADMIN' : 'FORM');

  // Si l'utilisateur n'est pas connecté, afficher la vue d'authentification
  if (!isLoggedIn) {
    return (
      <AppLayout>
        <AuthView />
      </AppLayout>
    );
  }

  // Si l'utilisateur est connecté
  const renderView = () => {
    switch (currentTab) {
      case 'FORM':
        return <UserFormView />;
      case 'ADMIN':
        // Seuls les admins peuvent voir le dashboard
        return isAdmin ? <AdminDashboardView /> : <AccessDeniedView />;
      default:
        return isAdmin ? <AdminDashboardView /> : <UserFormView />;
    }
  };
  
  // Vue pour les utilisateurs non-admin tentant d'accéder au dashboard
  const AccessDeniedView = () => (
    <div className="max-w-xl mx-auto p-6 bg-white rounded-xl shadow-2xl border border-red-200">
        <header className="flex items-center space-x-3 border-b pb-4 mb-6">
          <XCircle className="w-7 h-7 text-red-700" />
          <h2 className="text-2xl font-extrabold text-red-900">Accès Refusé</h2>
        </header>
        <p className="text-gray-700">Vous devez avoir le rôle 'ADMIN' pour accéder à cette ressource.</p>
    </div>
  );
  
  // Navigation principale pour l'utilisateur connecté
  const MainNav = () => (
     <nav className="flex space-x-4 mb-6">
        {/* L'onglet Admin n'est visible que si l'utilisateur est Admin */}
        {isAdmin && (
           <TabButton 
              icon={LayoutDashboard} 
              label="Admin Dashboard" 
              isActive={currentTab === 'ADMIN'}
              onClick={() => setCurrentTab('ADMIN')}
           />
        )}
        <TabButton 
          icon={User} 
          label="Nouvelle Demande" 
          isActive={currentTab === 'FORM'}
          onClick={() => setCurrentTab('FORM')}
        />
     </nav>
  );

  const TabButton = ({ icon: Icon, label, isActive, onClick }) => (
    <button
      onClick={onClick}
      className={`flex items-center space-x-3 p-3 rounded-xl transition-all duration-300 ${
        isActive
          ? 'bg-white text-blue-800 shadow-lg font-bold border border-blue-200'
          : 'bg-gray-200 text-gray-600 hover:bg-gray-300'
      }`}
    >
      <Icon className="w-5 h-5" />
      <span>{label}</span>
    </button>
  );


  return (
    <AppLayout>
      <MainNav />
      {renderView()}
    </AppLayout>
  );
};

// L'application est enveloppée dans le AuthProvider
const App = () => (
  <AuthProvider>
    <RootApp />
  </AuthProvider>
);

export default App;